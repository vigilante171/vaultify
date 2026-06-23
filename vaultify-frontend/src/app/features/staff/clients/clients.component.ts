import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { timeout } from 'rxjs';
import { ApiService, ClientResponse } from '../../../core/services/api.service';

@Component({
  selector: 'app-staff-clients',
  standalone: true,
  templateUrl: './clients.component.html',
  styleUrl: './clients.component.scss'
})
export class ClientsComponent implements OnInit {
  clients: ClientResponse[] = [];
  loading = false;
  errorMessage = '';

  constructor(
    private apiService: ApiService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadClients();
  }

  loadClients(): void {
    this.loading = true;
    this.errorMessage = '';
    this.cdr.detectChanges();

    this.apiService.getStaffClients().pipe(timeout(8000)).subscribe({
      next: (response) => {

        this.clients = response || [];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {

        this.loading = false;
        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Could not load clients.';
        this.cdr.detectChanges();
      }
    });
  }
}

