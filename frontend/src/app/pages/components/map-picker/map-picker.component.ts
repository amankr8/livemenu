import {
  Component,
  ElementRef,
  EventEmitter,
  inject,
  Input,
  Output,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { Icons } from '../../../utils/icons';
import { CommonModule } from '@angular/common';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { LocationService } from '../../../service/location.service';
import { UiService } from '../../../service/ui.service';

@Component({
  selector: 'app-map-picker',
  imports: [CommonModule, FontAwesomeModule],
  templateUrl: './map-picker.component.html',
})
export class MapPickerComponent {
  @Input() disabled = false;
  @Input() lat!: number;
  @Input() lng!: number;
  @Input() isLocating = false;

  @Output() locationChange = new EventEmitter<{
    lat: number;
    lng: number;
  }>();

  @Output() locating = new EventEmitter<boolean>();

  icons = Icons;

  @ViewChild('mapContainer', { static: true })
  mapContainer!: ElementRef<HTMLDivElement>;

  private locationService = inject(LocationService);
  private uiService = inject(UiService);

  map!: google.maps.Map;

  async ngAfterViewInit() {
    const { Map } = await this.locationService.getMapsLibrary();

    this.map = new Map(this.mapContainer.nativeElement, {
      center: { lat: this.lat, lng: this.lng },
      zoom: 16,
      disableDefaultUI: true,
      gestureHandling: 'greedy',
    });

    this.map.addListener('idle', () => {
      if (this.isLocating) return;

      const center = this.map.getCenter();
      if (!center) return;

      this.locationChange.emit({
        lat: center.lat(),
        lng: center.lng(),
      });
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    if (!this.map) return;

    if (changes['lat'] || changes['lng']) {
      this.map.panTo({
        lat: this.lat,
        lng: this.lng,
      });
    }

    if (changes['disabled']) {
      this.map.setOptions({
        gestureHandling: this.disabled ? 'none' : 'greedy',
        clickableIcons: !this.disabled,
      });
    }
  }

  locateMe() {
    if (!navigator.geolocation) {
      this.uiService.showToast('Geolocation not supported', 'error');
      return;
    }

    this.locating.emit(true);

    navigator.geolocation.getCurrentPosition(
      (position) => {
        const { latitude, longitude } = position.coords;
        this.map.panTo({ lat: latitude, lng: longitude });

        this.locationChange.emit({
          lat: latitude,
          lng: longitude,
        });
        this.locating.emit(false);
      },
      (error) => {
        this.locating.emit(false);
        this.handleLocationError(error);
      },
    );
  }

  private handleLocationError(error: GeolocationPositionError) {
    let message = 'An unknown error occurred';
    switch (error.code) {
      case error.PERMISSION_DENIED:
        message = 'Please allow location access in your browser settings';
        break;
      case error.POSITION_UNAVAILABLE:
        message = 'Location information is unavailable';
        break;
      case error.TIMEOUT:
        message = 'The request to get user location timed out';
        break;
    }
    this.uiService.showToast(message, 'error');
  }
}
