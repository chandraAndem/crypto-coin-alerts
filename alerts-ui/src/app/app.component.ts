import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Crypto Coin Alerts';

  constructor(translate: TranslateService) {
    translate.setDefaultLang('en');

    // TODO: choose lang based on the user preferences
    translate.use('en');

    // define langs
    translate.setTranslation('en', this.englishLang());
  }

  englishLang(): Object {
    return {
      // default messages from angular
      'required': 'a value is required',
      'email': 'invalid email',
      'minlength': 'more characters are required',
      'maxlength': 'too many characters',
      'min': 'the value is too small',
      'max': 'the value is too big',
      // labels
      'label.verifyingEmail': 'Verifying email',
      'label.login': 'Log in',
      'label.logout': 'Log out',
      'label.newAccount': 'New account',
      'label.fixedPriceAlerts': 'Fixed price alerts',
      // messages
      'message.welcome': 'Welcome',
      'message.bye': 'You are now logged out',
      'message.authError': 'You are not authorized, please log in',
      'message.verifyEmail': 'An email has been sent to you inbox, please click on the link to verify your email',
      'message.emailVerified': 'Thanks for verifying your email',
      'message.resolveCaptcha': 'Resolve the reCAPTCHA',
      'message.alertCreated': 'Your alert was created',
      'message.confirmDeleteAlert': 'The alert is going to be deleted, continue?',
      'message.alertDeleted': 'Alert deleted',
      'message.yourAboveFixedPriceAlert': 'You will be notified when {{currency}} is above {{price}} {{market}} on {{exchange}}',
      'message.yourBelowFixedPriceAlert': 'You will be notified when {{currency}} is below {{price}} {{market}} on {{exchange}}',
      // field names
      'field.email': 'Email',
      'field.password': 'Password',
      'field.repeatPassword': 'Repeat password',
      'field.exchange': 'Exchange',
      'field.market': 'Market',
      'field.currency': 'Currency',
      'field.priceCondition': 'Price condition',
      'field.above': 'Above',
      'field.below': 'Below',
      'field.price': 'Price',
      'field.basePrice': 'The price when you bought / sold',
      'field.optional': 'optional',
      // actions
      'action.createAccount': 'Create account',
      'action.login': 'Log in',
      'action.createAlert': 'Create alert',
      'action.newAlert': 'New alert',
      'action.cancel': 'Cancel',
      'action.delete': 'Delete',
      // custom validations
      'validation.passwordMismatch': 'the password does not match'
    };
  }
}
