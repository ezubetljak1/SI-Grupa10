import { Component, OnInit, inject } from '@angular/core';
import { AppShellComponent } from './shared/layout/app-shell/app-shell';
import { AuthService } from './auth/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [AppShellComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit {
  private readonly authService = inject(AuthService);

  ngOnInit(): void {
    void this.authService.init();
  }
}
