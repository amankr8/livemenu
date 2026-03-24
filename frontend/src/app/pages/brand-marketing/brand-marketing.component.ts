import { Component } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Icons } from '../../utils/icons';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-brand-marketing',
  imports: [FontAwesomeModule, RouterLink],
  templateUrl: './brand-marketing.component.html',
})
export class BrandMarketingComponent {
  icons = Icons;
}
