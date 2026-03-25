import { Component } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Icons } from '../../../utils/icons';

@Component({
  selector: 'app-contact-us',
  imports: [FontAwesomeModule],
  templateUrl: './contact-us.component.html',
})
export class ContactUsComponent {
  icons = Icons;
}
