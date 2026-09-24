import { Component, EventEmitter, Input, Output, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ImportService, ImportResult } from './import.service';

@Component({
  selector: 'app-import-button',
  standalone: true,
  imports: [CommonModule],
  template: `
    <input #fileInput type="file" accept=".xml,.json,.csv" style="display: none" (change)="onFileSelected($event)" />
    <button type="button" class="btn btn-outline-primary" [disabled]="loading" (click)="fileInput.click()">
      <span *ngIf="!loading">Импорт {{ entityLabel }}</span>
      <span *ngIf="loading">Импортируем...</span>
    </button>

    <div *ngIf="lastResult" class="mt-2 alert" [ngClass]="lastResult.failedCount > 0 ? 'alert-warning' : 'alert-success'">
      Импортировано: {{ lastResult.importedCount }} из {{ lastResult.totalRecords }}.
      <span *ngIf="lastResult.failedCount > 0">Ошибок: {{ lastResult.failedCount }}.</span>
      <ul *ngIf="lastResult.errors.length > 0" class="mb-0">
        <li *ngFor="let err of lastResult.errors">{{ err }}</li>
      </ul>
    </div>

    <div *ngIf="errorMessage" class="mt-2 alert alert-danger">{{ errorMessage }}</div>
  `,
})
export class ImportButtonComponent {
  @Input({ required: true }) entityType!: string;

  @Input() entityLabel = '';

  @Output() imported = new EventEmitter<ImportResult>();

  @ViewChild('fileInput') fileInputRef!: ElementRef<HTMLInputElement>;

  loading = false;
  lastResult: ImportResult | null = null;
  errorMessage: string | null = null;

  constructor(private importService: ImportService) {}

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) {
      return;
    }

    this.loading = true;
    this.lastResult = null;
    this.errorMessage = null;

    this.importService.importFile(this.entityType, file).subscribe({
      next: result => {
        this.loading = false;
        this.lastResult = result;
        this.imported.emit(result);
        input.value = '';
      },
      error: err => {
        this.loading = false;
        this.errorMessage = err?.error?.message ?? 'Не удалось импортировать файл';
        input.value = '';
      },
    });
  }
}
