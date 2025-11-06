import { Component, OnInit, signal, WritableSignal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';

interface MediaItem {
  id: number;
  type: 'svg' | 'png';
  content: string; // Holds SVG string or PNG URL
  size: number;
  duration: number;
  delay: number;
  blur: number;
  windIntensity: number;
  color: string;
  startX: number;
  opacity: number; // Added opacity property
}

interface MediaSource {
  type: 'svg' | 'png';
  content: string;
}

@Component({
  selector: 'app-auth-layout',
  standalone: true,
  imports: [RouterOutlet, CommonModule],
  template: `
    <div class="pattern-rain-container">
      @for (item of rainItems(); track item.id) {
        <div
          class="rain-hexagon"
          [style.width.px]="item.size"
          [style.height.px]="item.size"
          [style.left.%]="item.startX"
          [style.animationDelay.s]="item.delay"
          [style.--animation-duration.s]="item.duration"
          [style.filter]="'blur(' + item.blur + 'px)'"
          [style.--wind-intensity.px]="item.windIntensity"
          [style.border]="item.type === 'svg' ? '1px solid ' + item.color : 'none'"
          [style.boxShadow]="'0 0 10px ' + item.color + ', 0 0 20px ' + item.color"
        >
          @if (item.type === 'svg') {
            <div [innerHTML]="item.content"></div>
          } @else if (item.type === 'png') {
            <img [src]="item.content" [alt]="'rain item'" />
          }
        </div>
      }
    </div>
    <router-outlet></router-outlet>
  `,
  styleUrls: ['../app.css'] // Re-use the app.css for animation styles
})
export class AuthLayoutComponent implements OnInit {
  rainItems: WritableSignal<MediaItem[]> = signal([]);

  private mediaCollection: MediaSource[] = [
    {
      type: 'png',
      content: 'fav-logo.png'
    }, 
    {
      type: 'png',
      content: 'hexagon-svg.svg'
    },
    {
      type: 'png',
      content: 'hexagon-svg2.svg'
    },
    {
      type: 'png',
      content: 'hexagon-svg1.svg'
    },
    {
      type: 'png',
      content: 'logo-picto.png'
    }
  ];
  private neonColors = ['var(--color-neon-purple)', 'var(--color-link-blue)', 'var(--color-cta-orange-start)'];

  async ngOnInit(): Promise<void> {
    await this.generateRainItems(20); 
  }

  async generateRainItems(count: number): Promise<void> {
    const items: MediaItem[] = [];
    for (let i = 0; i < count; i++) {
      const media = this.mediaCollection[Math.floor(Math.random() * this.mediaCollection.length)];
      const size = Math.random() * (30 - 5) + 5; 
      const duration = Math.random() * (45 - 25) + 25; 
      const blur = (1 - (size / 60)) * 4; 
      const windIntensity = Math.random() * (60 - 20) + 20; 
      const color = this.neonColors[i % this.neonColors.length];
      const startX = Math.random() * 100; 
      const delay = Math.random() * 15; 
      const opacity = Math.random() * (0.5 - 0.2) + 0.2;

      items.push({
        id: i,
        type: media.type,
        content: media.content,
        size,
        duration,
        delay,
        blur,
        windIntensity,
        color,
        startX,
        opacity,
      });
      this.rainItems.set([...items]); // Update signal after each item
      await new Promise(resolve => setTimeout(resolve, 1000)); // 1-second pause
    }
  }
}
