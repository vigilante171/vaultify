import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { timeout } from 'rxjs';
import { ApiService, AuditLogResponse } from '../../../core/services/api.service';

@Component({
  selector: 'app-super-admin-audit-logs',
  standalone: true,
  templateUrl: './audit-logs.component.html',
  styleUrl: './audit-logs.component.scss'
})
export class AuditLogsComponent implements OnInit {
  logs: AuditLogResponse[] = [];
  loading = false;
  errorMessage = '';

  constructor(
    private apiService: ApiService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadLogs();
  }

  getLogId(log: AuditLogResponse): string {
    const item = log as any;
    return item.id || item.auditLogId || item._id || '';
  }

  loadLogs(): void {
    this.loading = true;
    this.errorMessage = '';
    this.cdr.detectChanges();

    this.apiService.getAuditLogs().pipe(timeout(8000)).subscribe({
      next: (response) => {

        this.logs = response || [];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {

        this.loading = false;
        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Could not load audit logs.';

        this.cdr.detectChanges();
      }
    });
  }
}

