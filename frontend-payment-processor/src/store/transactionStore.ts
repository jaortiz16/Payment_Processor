import { create } from 'zustand';

interface Banco {
  codigo: number;
  nombreComercial: string;
}

interface Transaccion {
  codTransaccion: number;
  monto: number;
  modalidad: string;
  codigoMoneda: string;
  marca: string;
  banco: Banco;
  estado: string;
  detalle: string;
}

interface Transaction {
  codHistorialEstado: number;
  codTransaccion: string;
  estado: string;
  fechaEstadoCambio: string;
  detalle: string;
  transaccion: Transaccion;
}

interface TransactionStore {
  transactions: Transaction[];
  isLoading: boolean;
  error: string | null;
  fetchTransactions: (params: {
    estado?: string;
    fechaInicio?: string;
    fechaFin?: string;
    bancoNombre?: string;
  }) => Promise<void>;
}

const API_URL = 'http://localhost:8080';

export const useTransactionStore = create<TransactionStore>((set) => ({
  transactions: [],
  isLoading: false,
  error: null,
  fetchTransactions: async (params) => {
    set({ isLoading: true, error: null });
    try {
      const queryParams = new URLSearchParams();
      if (params.estado && params.estado !== 'ALL') queryParams.append('estado', params.estado);
      if (params.fechaInicio) queryParams.append('fechaInicio', params.fechaInicio);
      if (params.fechaFin) queryParams.append('fechaFin', params.fechaFin);
      if (params.bancoNombre) queryParams.append('bancoNombre', params.bancoNombre);

      const url = `${API_URL}/api/v1/historial-estados/transacciones?${queryParams}`;
      console.log('Fetching transactions from:', url);

      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        credentials: 'include',
        mode: 'cors'
      });

      console.log('Response status:', response.status);
      
      if (!response.ok) {
        const errorData = await response.text();
        console.error('Error response:', errorData);
        throw new Error(errorData || 'Error al cargar las transacciones');
      }

      const data = await response.json();
      console.log('Response data:', data);

      set({ transactions: data || [], isLoading: false });
    } catch (error) {
      console.error('Error fetching transactions:', error);
      set({ 
        error: error instanceof Error ? error.message : 'Error al conectar con el servidor', 
        isLoading: false,
        transactions: [] 
      });
    }
  },
})); 