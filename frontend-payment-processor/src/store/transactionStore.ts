import { create } from 'zustand';
import axios from 'axios';

export interface Transaction {
  code: number;
  transaccion: {
    codigo: number;
    monto: number;
    modalidad: string;
    codigoMoneda: string;
    marca: string;
    banco: {
      codigo: number;
      nombreComercial: string;
    };
  };
  estado: string;
  fechaEstadoCambio: string;
  detalle: string | null;
}

interface TransactionState {
  transactions: Transaction[];
  isLoading: boolean;
  error: string | null;
  fetchTransactions: (params: {
    estado?: string;
    fechaInicio?: string;
    fechaFin?: string;
    bancoNombre?: string;
  }) => Promise<void>;
  fetchTransactionsByDate: (fecha: string) => Promise<void>;
}

const API_URL = 'http://3.14.250.222/api/v1/historial-estados';

export const useTransactionStore = create<TransactionState>((set) => ({
  transactions: [],
  isLoading: false,
  error: null,
  fetchTransactions: async ({ estado, fechaInicio, fechaFin, bancoNombre }) => {
    try {
      set({ isLoading: true, error: null });
      
      let url = `${API_URL}/transacciones`;
      const params = new URLSearchParams();
      
      if (estado && estado !== 'all') params.append('estado', estado);
      if (fechaInicio) {
        const date = new Date(fechaInicio);
        date.setHours(0, 0, 0, 0);
        params.append('fechaInicio', date.toISOString());
      }
      if (fechaFin) {
        const date = new Date(fechaFin);
        date.setHours(23, 59, 59, 999);
        params.append('fechaFin', date.toISOString());
      }
      if (bancoNombre) params.append('bancoNombre', bancoNombre);
      
      if (params.toString()) {
        url += `?${params.toString()}`;
      }
      
      const response = await axios.get(url);
      set({ transactions: response.data, isLoading: false });
    } catch (error) {
      set({ 
        error: error instanceof Error ? error.message : 'Error al cargar las transacciones', 
        isLoading: false 
      });
    }
  },
  fetchTransactionsByDate: async (fecha: string) => {
    try {
      set({ isLoading: true, error: null });
      
      const response = await axios.get(`${API_URL}/transacciones/fecha?fecha=${fecha}`);
      set({ transactions: response.data, isLoading: false });
    } catch (error) {
      set({ 
        error: error instanceof Error ? error.message : 'Error al cargar las transacciones', 
        isLoading: false 
      });
    }
  },
})); 