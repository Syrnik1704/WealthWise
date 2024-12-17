
import { Component, ChangeDetectionStrategy, inject, ChangeDetectorRef } from "@angular/core";
import { CategoriesApiService } from "../../shared/services/categories-service";
import { Category } from "../../shared";
import { Observable, of } from "rxjs";
import { AsyncPipe, CommonModule } from "@angular/common";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatSelectModule } from "@angular/material/select";
import { MatTooltipModule } from "@angular/material/tooltip";
import { TranslateModule } from "@ngx-translate/core";
import { AdminService } from "../services/admin.service";
import { MatSnackBar } from "@angular/material/snack-bar";
import { HttpErrorResponse } from "@angular/common/http";
import { UserData } from "../models/usersData";

@Component({
    selector: 'admin-panel',
    standalone: true,
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        AsyncPipe,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        TranslateModule,
        MatButtonModule,
        MatTooltipModule,
        AsyncPipe,
        FormsModule
    ],
    templateUrl: './admin-panel.component.html',
    styleUrl: './admin-panel.component.scss',
})
export class AdminPanelComponent {
    private readonly cdRef = inject(ChangeDetectorRef);
    protected categories$: Observable<Category[]> = of([]);
    protected users$: Observable<UserData[]> = of([]);
    categoryInput: string = '';
    categories: string[] = [];
    selectedCategory: string = '';
    selectedUser: string = '';
    constructor(private snackBar: MatSnackBar, private adminService: AdminService, private categoriesApiService: CategoriesApiService) {
        this.categories$ = inject(CategoriesApiService).getCategories();
    }

    ngOnInit(): void {
        this.categories$.subscribe((categories) => {
            this.categories = categories.map((category) => category.name);
            if (this.categories.length > 0) {
                this.selectedCategory = this.categories[0]; // Ustaw domyślną wartość
            }
        });

        this.users$ = this.adminService.getUsers();
    }

    onAddCategory() {
        const newCategory = this.categoryInput;
        if (newCategory.length < 3) {
            this.snackBar.open(`Nazwa Kategorii jest za krótka`, '', {
                duration: 6000,
                panelClass: ['error-toast']
            });
            return;
        }
        this.adminService.addCategory({ name: newCategory }).subscribe({
            next: () => {
                this.snackBar.open('Kategoria została utworzona', '', {
                    duration: 6000,
                    panelClass: ['success-toast']
                });
                this.categoryInput = '';
                this.refreshCategories();
            },
            error: (error: HttpErrorResponse) => {
                this.snackBar.open(`Kategoria '${newCategory}' już istnieje!`, '', {
                    duration: 6000,
                    panelClass: ['error-toast']
                });
            },
        });
    }

    onRemoveCategory() {
        if (this.selectedCategory) {
            this.adminService.deleteCategory({ name: this.selectedCategory }).subscribe({
                next: () => {
                    this.snackBar.open('Kategoria została usunięta', '', {
                        duration: 6000,
                        panelClass: ['success-toast']
                    });
                    this.refreshCategories();
                },
                error: (error: HttpErrorResponse) => {
                    this.snackBar.open(`Kategoria nie została usunięta`, '', {
                        duration: 6000,
                        panelClass: ['error-toast']
                    });
                },
            });
        }
    }

    refreshCategories() {
        this.categories$ = this.categoriesApiService.getCategories(); // Reassign the observable
        this.categories$.subscribe((categories) => {
            this.categories = categories.map((category) => category.name);
            if (this.categories.length > 0) {
                this.selectedCategory = this.categories[0]; // Set default selected value
            }
            this.cdRef.detectChanges(); // Force detect changes
        });
    }

    onBlockUser() {
        if (this.selectedUser) {
            this.adminService.blockUser({ emails: [this.selectedUser] }).subscribe({
                next: () => {
                    this.snackBar.open('Użytkownik został zablokowany', '', {
                        duration: 6000,
                        panelClass: ['success-toast']
                    });
                    this.refreshUsers();
                },
                error: (error: HttpErrorResponse) => {
                    this.snackBar.open(`Użytkownik nie został zablokowany`, '', {
                        duration: 6000,
                        panelClass: ['error-toast']
                    });
                },
            });
        }
    }

    onUnblockUser() {
        if (this.selectedUser) {
            this.adminService.unblockUser({ emails: [this.selectedUser] }).subscribe({
                next: () => {
                    this.snackBar.open('Użytkownik został odblokowany', '', {
                        duration: 6000,
                        panelClass: ['success-toast']
                    });
                    this.refreshUsers();
                },
                error: (error: HttpErrorResponse) => {
                    this.snackBar.open(`Użytkownik nie został odblokowany`, '', {
                        duration: 6000,
                        panelClass: ['error-toast']
                    });
                },
            });
        }
    }

    refreshUsers() {
        this.users$ = this.adminService.getUsers();
        this.cdRef.detectChanges(); 
    }

}