import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { timeout } from 'rxjs';
import {
  ApiService,
  ClientResponse,
  CreateClientRequest,
  Gender
} from '../../../core/services/api.service';

@Component({
  selector: 'app-owner-clients',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './clients.component.html',
  styleUrl: './clients.component.scss'
})
export class ClientsComponent implements OnInit {
  clients: ClientResponse[] = [];
  loading = false;
  saving = false;
  errorMessage = '';
  successMessage = '';
  showCreateForm = false;

  genders: Gender[] = ['MALE', 'FEMALE', 'OTHER'];

  form: CreateClientRequest = {
    fullName: '',
    phone: '',
    email: '',
    gender: 'MALE',
    notes: ''
  };

  constructor(
    private apiService: ApiService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadClients();
  }

  getClientId(client: ClientResponse): string {
    const item = client as any;
    return item.id || item.clientId || item._id || '';
  }

  loadClients(): void {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.detectChanges();

    this.apiService.getOwnerClients().pipe(timeout(8000)).subscribe({
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

  createClient(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.form.fullName || !this.form.phone) {
      this.errorMessage = 'Client name and phone are required.';
      this.cdr.detectChanges();
      return;
    }

    this.saving = true;
    this.cdr.detectChanges();

    this.apiService.createOwnerClient(this.form).pipe(timeout(8000)).subscribe({
      next: () => {
        this.saving = false;
        this.successMessage = 'Client created successfully.';
        this.showCreateForm = false;

        this.form = {
          fullName: '',
          phone: '',
          email: '',
          gender: 'MALE',
          notes: ''
        };

        this.cdr.detectChanges();
        this.loadClients();
      },
      error: (error) => {

        this.saving = false;
        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Could not create client.';

        this.cdr.detectChanges();
      }
    });
  }

  disableClient(client: ClientResponse): void {
    const clientId = this.getClientId(client);

    if (!clientId) {
      this.errorMessage = 'Client ID is missing.';
      this.cdr.detectChanges();
      return;
    }

    this.apiService.deleteOwnerClient(clientId).pipe(timeout(8000)).subscribe({
      next: () => {
        this.successMessage = 'Client disabled successfully.';
        this.cdr.detectChanges();
        this.loadClients();
      },
      error: (error) => {

        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Could not disable client.';

        this.cdr.detectChanges();
      }
    });
  }
}

