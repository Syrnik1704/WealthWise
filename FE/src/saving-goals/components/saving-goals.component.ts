import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'ww-saving-goals',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush
  imports: [],
  template: `
    <p>
      saving-goals works!
    </p>
  `,
  styles: ``
})
export class SavingGoalsComponent {

}
