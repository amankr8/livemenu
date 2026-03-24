import { Component } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Icons } from '../../utils/icons';

@Component({
  selector: 'app-brand-marketing',
  imports: [FontAwesomeModule],
  templateUrl: './brand-marketing.component.html',
})
export class BrandMarketingComponent {
  icons = Icons;
}
