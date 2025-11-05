import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HexagonRain } from './hexagon-rain';

describe('HexagonRain', () => {
  let component: HexagonRain;
  let fixture: ComponentFixture<HexagonRain>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HexagonRain]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HexagonRain);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
