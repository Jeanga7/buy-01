import { Component, OnInit, ElementRef, Renderer2 } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.html', // Changed to templateUrl
  styleUrl: './app.css' // Added styleUrl
})
export class App implements OnInit {
  private svgHexagon = `<?xml version="1.0" encoding="utf-8"?><!-- Uploaded to: SVG Repo, www.svgrepo.com, Generator: SVG Repo Mixer Tools -->
<svg width="800px" height="800px" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
<path d="M20.9485 11.0195C21.2909 11.6283 21.2909 12.3717 20.9485 12.9805L17.5735 18.9805C17.2192 19.6103 16.5529 20 15.8303 20H8.16969C7.44715 20 6.78078 19.6103 6.42654 18.9805L3.05154 12.9805C2.70908 12.3717 2.70908 11.6283 3.05154 11.0195L6.42654 5.01948C6.78078 4.38972 7.44715 4 8.16969 4H15.8303C16.5529 4 17.2192 4.38972 17.5735 5.01948L20.9485 11.0195Z" stroke="#000000" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
</svg>`;

  private svgHexagon1 = `<?xml version='1.0' encoding='utf-8'?>
<!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.1//EN' 'http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd'>
<!-- Uploaded to: SVG Repo, www.svgrepo.com, Generator: SVG Repo Mixer Tools -->
<svg fill="#000000" height="800px" width="800px" version="1.1" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512" xmlns:xlink="http://www.w3.org/1999/xlink" enable-background="new 0 0 512 512">
  <g>
    <g>
      <path d="m365.5,191.5v-106.7c0-7.2-3.8-13.8-9.9-17.5l-89-53.4c-6.5-3.9-14.5-3.9-21,0l-89.1,53.4c-6.2,3.7-9.9,10.3-9.9,17.5v106.7c0,7.2 3.8,13.8 9.9,17.5l89.1,53.4c9.4,5.4 17.2,2.5 21,0l89-53.4c6.1-3.7 9.9-10.3 9.9-17.5z"/>
      <path d="m220,303l-89-53.4c-6.5-3.9-14.5-3.9-21,0l-89.1,53.4c-6.2,3.7-9.9,10.3-9.9,17.5v106.7c0,7.2 3.8,13.8 9.9,17.5l89.1,53.4c9.4,5.4 17.2,2.5 21,0l89-53.4c6.2-3.7 9.9-10.3 9.9-17.5v-106.7c0-7.2-3.7-13.8-9.9-17.5z"/>
      <path d="m491.1,302.9l-89-53.4c-6.5-3.9-14.5-3.9-21,0l-89.1,53.4c-6.2,3.7-9.9,10.3-9.9,17.5v106.7c0,7.2 3.8,13.8 9.9,17.5l89,53.4c9.4,5.4 17.2,2.5 21,0l89-53.4c6.2-3.7 9.9-10.3 9.9-17.5v-106.7c0.1-7.2-3.7-13.8-9.8-17.5z"/>
    </g>
  </g>
</svg>`;

  private svgHexagon2 = `<?xml version="1.0" encoding="iso-8859-1"?>
<!-- Uploaded to: SVG Repo, www.svgrepo.com, Generator: SVG Repo Mixer Tools -->
<svg fill="#000000" height="800px" width="800px" version="1.1" id="Capa_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" 
	 viewBox="0 0 490 490" xml:space="preserve">
<g>
	<path d="M466.032,367.5v-245L245,0L23.968,122.5v245L245,490L466.032,367.5z M118.865,175.094L245,105.188l126.136,69.906v139.813
		L245,384.813l-126.135-69.906V175.094z"/>
	<polygon points="331.299,292.828 331.299,197.172 245,149.343 158.701,197.172 158.701,292.828 245,340.657 	"/>
</g>
</svg>`;

  private svgCollection = [this.svgHexagon, this.svgHexagon1, this.svgHexagon2];
  private neonColors = ['#7C3AED', '#FF9E3D', '#FF7E3D', '#1B4FFF']; // Using colors from styles.css :root

  constructor(private el: ElementRef, private renderer: Renderer2) {}

  ngOnInit(): void {
    this.startNeonRain();
  }

  startNeonRain(): void {
    const container = this.el.nativeElement.querySelector('#neon-rain-container');
    if (!container) {
      console.error('Neon rain container not found!');
      return;
    }

    const numberOfDrops = 50; // Adjust as needed

    for (let i = 0; i < numberOfDrops; i++) {
      this.generateRaindrop(container);
    }
  }

  generateRaindrop(container: HTMLElement): void {
    const svgContent = this.svgCollection[Math.floor(Math.random() * this.svgCollection.length)];
    const size = Math.random() * (25 - 3) + 3; // Size between 5px and 20px
    const startX = Math.random() * window.innerWidth;
    const duration = Math.random() * (45 - 10) + 10; // Duration between 5s and 10s
    const delay = Math.random() * 10; // Delay up to 5s
    const color = this.neonColors[Math.floor(Math.random() * this.neonColors.length)];

    const drop = this.renderer.createElement('div');
    this.renderer.addClass(drop, 'neon-raindrop');
    this.renderer.setStyle(drop, 'left', `${startX}px`);
    this.renderer.setStyle(drop, 'width', `${size}px`);
    this.renderer.setStyle(drop, 'height', `${size}px`);
    this.renderer.setStyle(drop, 'animationDuration', `${duration}s`);
    this.renderer.setStyle(drop, 'animationDelay', `${delay}s`);
    this.renderer.setStyle(drop, 'position', 'absolute');
    this.renderer.setStyle(drop, 'top', `-${size}px`); // Start above the viewport, based on its size
    this.renderer.setStyle(drop, 'pointer-events', 'none'); // Don't interfere with clicks
    this.renderer.setStyle(drop, 'opacity', `0`); // Start with 0 opacity for fade-in

    // Inject SVG content and set fill color
    drop.innerHTML = svgContent;
    const svgElement = drop.querySelector('svg');
    if (svgElement) {
      this.renderer.setStyle(svgElement, 'fill', color);
      this.renderer.setStyle(svgElement, 'stroke', color); // Assuming stroke might also be used
      this.renderer.setStyle(svgElement, 'filter', `drop-shadow(0 0 5px ${color})`); // Neon glow
      this.renderer.setStyle(svgElement, 'width', '100%'); // Make SVG fill its container
      this.renderer.setStyle(svgElement, 'height', '100%'); // Make SVG fill its container
    }

    this.renderer.appendChild(container, drop);

    // Trigger fade-in after appending to DOM
    setTimeout(() => {
      this.renderer.setStyle(drop, 'opacity', `${Math.random() * (0.5 - 0.2) + 0.2}`); // Random opacity
    }, 50);

    // Reset position when animation ends
    drop.addEventListener('animationiteration', () => {
      this.renderer.setStyle(drop, 'left', `${Math.random() * window.innerWidth}px`);
      this.renderer.setStyle(drop, 'animationDelay', `0s`); // Reset delay after first iteration
      this.renderer.setStyle(drop, 'opacity', `0`); // Reset opacity for next fade-in
      setTimeout(() => {
        this.renderer.setStyle(drop, 'opacity', `${Math.random() * (0.5 - 0.2) + 0.2}`); // Random opacity
      }, 50);
    });
  }
}
