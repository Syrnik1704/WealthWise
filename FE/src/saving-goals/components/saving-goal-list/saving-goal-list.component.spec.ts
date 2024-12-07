import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SavingGoalListComponent } from './saving-goal-list.component';

describe('SavingGoalListComponent', () => {
  let component: SavingGoalListComponent;
  let fixture: ComponentFixture<SavingGoalListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SavingGoalListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SavingGoalListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
