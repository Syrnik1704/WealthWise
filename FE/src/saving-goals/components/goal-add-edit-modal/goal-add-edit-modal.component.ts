import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { TranslateModule } from '@ngx-translate/core';
import { GoalAddEditForm, GoalAddEditFormKeys } from './goal-add-edit-form.model';

@Component({
  selector: 'ww-goal-add-edit-modal',
  standalone: true,
  imports: [
    MatDialogModule,
    MatButtonModule,
    MatFormField,
    MatInput,
    MatLabel,
    TranslateModule,
    NgIf,
    ReactiveFormsModule,
    MatDatepickerModule,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './goal-add-edit-modal.component.html',
})
export class GoalAddEditModalComponent {
  protected modelForm?: FormGroup<GoalAddEditForm>;
  protected readonly GoalAddEditFormKeys = GoalAddEditFormKeys;
  protected isEdit = false;
}
