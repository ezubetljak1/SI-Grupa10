import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import Keycloak, { KeycloakInstance, KeycloakProfile } from 'keycloak-js';
import { BehaviorSubject, Observable, catchError, map, of, tap } from 'rxjs';

import { ApiResponse } from '../../models/api.models';
import { RoleName, UserProfile } from '../models/auth.models';
import { keycloakConfig } from '../keycloak.config';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly keycloak: KeycloakInstance = new Keycloak({
    url: keycloakConfig.url,
    realm: keycloakConfig.realm,
    clientId: keycloakConfig.clientId,
  });

  private readonly initializedSubject = new BehaviorSubject<boolean>(false);
  private readonly authenticatedSubject = new BehaviorSubject<boolean>(false);
  private readonly profileSubject = new BehaviorSubject<UserProfile | null>(null);
  private readonly keycloakProfileSubject = new BehaviorSubject<KeycloakProfile | null>(null);

  readonly initialized$ = this.initializedSubject.asObservable();
  readonly authenticated$ = this.authenticatedSubject.asObservable();
  readonly profile$ = this.profileSubject.asObservable();
  readonly keycloakProfile$ = this.keycloakProfileSubject.asObservable();

  private initPromise: Promise<boolean> | null = null;

  init(): Promise<boolean> {
    if (this.initPromise) {
      return this.initPromise;
    }

    this.initPromise = this.keycloak
      .init({
        onLoad: 'check-sso',
        pkceMethod: 'S256',
        checkLoginIframe: false,
        silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
        silentCheckSsoFallback: false,
      })
      .then((authenticated) => {
        this.initializedSubject.next(true);
        this.authenticatedSubject.next(authenticated);

        if (authenticated) {
          this.loadKeycloakProfile();
          this.fetchCurrentUser().subscribe();
        }

        return authenticated;
      })
      .catch(() => {
        this.initializedSubject.next(true);
        this.authenticatedSubject.next(false);
        return false;
      });

    return this.initPromise;
  }

  login(returnUrl?: string): Promise<void> {
    return this.keycloak.login({
      redirectUri: this.resolveRedirectUri(returnUrl),
    });
  }

  logout(): Promise<void> {
    this.profileSubject.next(null);
    this.keycloakProfileSubject.next(null);
    this.authenticatedSubject.next(false);

    return this.keycloak.logout({
      redirectUri: window.location.origin + '/',
    });
  }

  isAuthenticated(): boolean {
    return this.authenticatedSubject.value && !!this.keycloak.token;
  }

  get profile(): UserProfile | null {
    return this.profileSubject.value;
  }

  hasRole(roles: RoleName | RoleName[]): boolean {
    const allowedRoles = Array.isArray(roles) ? roles : [roles];
    const profileRole = this.profileSubject.value?.role;

    if (profileRole && allowedRoles.includes(profileRole)) {
      return true;
    }

    const tokenRoles = this.readTokenRoles();
    return allowedRoles.some((role) => tokenRoles.includes(role));
  }

  getToken(): Promise<string | null> {
    if (!this.keycloak.token) {
      return Promise.resolve(null);
    }

    return this.keycloak
      .updateToken(30)
      .then(() => this.keycloak.token ?? null)
      .catch(() => null);
  }

  fetchCurrentUser(): Observable<UserProfile | null> {
    if (!this.keycloak.token) {
      this.profileSubject.next(null);
      return of(null);
    }

    return this.http.get<ApiResponse<UserProfile>>('/api/company/users/me').pipe(
      map((response) => response.payload),
      tap((profile) => this.profileSubject.next(profile)),
      catchError(() => {
        this.profileSubject.next(null);
        return of(null);
      })
    );
  }

  private loadKeycloakProfile(): void {
    this.keycloak
      .loadUserProfile()
      .then((profile) => this.keycloakProfileSubject.next(profile))
      .catch(() => this.keycloakProfileSubject.next(null));
  }

  private resolveRedirectUri(returnUrl?: string): string {
    if (!returnUrl || returnUrl === '/') {
      return window.location.origin + '/documents';
    }

    return new URL(returnUrl, window.location.origin).toString();
  }

  private readTokenRoles(): RoleName[] {
    const tokenParsed = this.keycloak.tokenParsed as
      | { realm_access?: { roles?: string[] }; resource_access?: Record<string, { roles?: string[] }> }
      | undefined;

    const realmRoles = tokenParsed?.realm_access?.roles ?? [];
    const clientRoles = tokenParsed?.resource_access?.[keycloakConfig.clientId]?.roles ?? [];
    const roles = new Set([...realmRoles, ...clientRoles]);

    return Array.from(roles).filter((role): role is RoleName =>
      ['ADMIN', 'OPERATOR', 'APPROVER', 'MANAGER'].includes(role)
    );
  }
}
