import { Component } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Icons } from '../../utils/icons';
import { RouterLink, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-livemenu',
  imports: [FontAwesomeModule, RouterOutlet, RouterLink],
  templateUrl: './livemenu.component.html',
})
export class LivemenuComponent {
  icons = Icons;
}
