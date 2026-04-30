import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-ui-card',
  standalone: true,
  templateUrl: './ui-card.html',
  styleUrl: './ui-card.scss'
})
export class UiCardComponent {
  @Input() title = '';
  @Input() subtitle = '';

  @Input() badgeText = '';
  @Input() badgeVariant: 'success' | 'warning' | 'error' | 'info' | 'processing' | 'neutral' = 'neutral';

  @Input() noPadding = false;
}