import {
  Component,
  computed,
  effect,
  ElementRef,
  inject,
  output,
  signal,
  viewChild,
} from '@angular/core';
import { AuthService } from '../../../service/auth.service';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { FormsModule } from '@angular/forms';
import { Icons } from '../../../utils/icons';
import { CommonModule } from '@angular/common';
import {
  Auth,
  RecaptchaVerifier,
  signInWithPhoneNumber,
  ConfirmationResult,
} from '@angular/fire/auth';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-otp-login',
  imports: [FontAwesomeModule, FormsModule, CommonModule],
  templateUrl: './otp-login.component.html',
})
export class OtpLoginComponent {
  phoneInput = viewChild<ElementRef<HTMLInputElement>>('phoneInput');
  otpInput = viewChild<ElementRef<HTMLInputElement>>('otpInput');

  private auth = inject(Auth);
  private authService = inject(AuthService);

  icons = Icons;

  step = signal<'phone' | 'otp'>('phone');
  loading = signal(false);
  error = signal<string | null>(null);
  countdown = signal(0);

  phoneNumber = '';
  otpValue = '';

  timerSubscription?: Subscription;
  recaptchaVerifier?: RecaptchaVerifier;
  confirmationResult?: ConfirmationResult;

  success = output<void>();
  close = output<void>();

  constructor() {
    effect(() => {
      const currentStep = this.step();

      setTimeout(() => {
        if (currentStep === 'phone') {
          this.phoneInput()?.nativeElement.focus();
        } else if (currentStep === 'otp') {
          this.otpInput()?.nativeElement.focus();
        }
      }, 0);
    });
  }

  ngAfterViewInit() {
    this.setupRecaptcha();
  }

  startTimer() {
    this.countdown.set(30);
    this.timerSubscription?.unsubscribe();
    this.timerSubscription = interval(1000).subscribe(() => {
      if (this.countdown() > 0) {
        this.countdown.update((v) => v - 1);
      } else {
        this.timerSubscription?.unsubscribe();
      }
    });
  }

  formattedCountdown = computed(() => {
    const count = this.countdown();
    return count < 10 ? `0${count}` : `${count}`;
  });

  setupRecaptcha() {
    if (this.recaptchaVerifier) {
      this.recaptchaVerifier.clear();
    }

    this.recaptchaVerifier = new RecaptchaVerifier(
      this.auth,
      'recaptcha-container',
      {
        size: 'invisible',
      },
    );
  }

  async sendOtp() {
    const recaptchaVerifier = this.recaptchaVerifier;
    if (!recaptchaVerifier) return;

    this.loading.set(true);
    this.error.set(null);
    try {
      const formattedPhone = `+91${this.phoneNumber}`;
      this.confirmationResult = await signInWithPhoneNumber(
        this.auth,
        formattedPhone,
        recaptchaVerifier,
      );
      this.step.set('otp');
      this.startTimer();
    } catch (error) {
      this.error.set('Failed to send SMS. Please try again.');
      this.setupRecaptcha();
    } finally {
      this.loading.set(false);
    }
  }

  async resendOtp() {
    this.setupRecaptcha();
    await this.sendOtp();
  }

  async verifyOtp() {
    this.loading.set(true);
    this.error.set(null);
    try {
      const result = await this.confirmationResult?.confirm(this.otpValue);
      const firebaseToken = await result?.user.getIdToken();

      if (firebaseToken) {
        this.authService.firebaseLogin(firebaseToken).subscribe({
          next: () => {
            this.cleanupRecaptcha();
            this.success.emit();
          },
          error: (err) => {
            this.loading.set(false);
            this.error.set('Some error occurred. Please try again');
            console.error('Backend Auth Failed', err);
          },
        });
      }
    } catch (error) {
      this.loading.set(false);
      this.error.set('Invalid code. Please check and try again.');
      this.otpValue = '';
    }
  }

  private cleanupRecaptcha() {
    if (this.recaptchaVerifier) {
      this.recaptchaVerifier.clear();
      this.recaptchaVerifier = undefined;
    }
  }

  resetToPhoneStep() {
    this.step.set('phone');
    this.otpValue = '';
    this.error.set(null);
    this.timerSubscription?.unsubscribe();
    this.countdown.set(0);
    this.setupRecaptcha();
  }

  ngOnDestroy() {
    this.timerSubscription?.unsubscribe();
    this.cleanupRecaptcha();
  }
}
