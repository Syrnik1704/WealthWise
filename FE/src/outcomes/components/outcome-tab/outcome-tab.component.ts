import { ChangeDetectionStrategy, Component, DestroyRef, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { OutcomeApiService } from '../../services/outcomes-api.service';

@Component({
  selector: 'ww-outcome-tab',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MatButtonModule],
  templateUrl: './outcome-tab.component.html',
  styles: [``],
})
export class OutcomeTabComponent {
  private readonly apiService = inject(OutcomeApiService);
  private readonly destroyRef = inject(DestroyRef);

  protected addOutcome(): void {}
}
