import { create } from 'zustand';

interface Banco {
  codigo: number;
  nombreComercial: string;
  razonSocial: string;
}

interface Transaccion {
  codigo: number;
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
  codigoTransaccion: number;
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

const API_URL = 'http://18.117.166.56';

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

      const url = `${API_URL}/v1/historial-estados?${queryParams}`;
      console.log('Fetching transactions from:', url);

      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        mode: 'cors'
      });

      if (!response.ok) {
        const errorData = await response.text();
        console.error('Error response:', errorData);
        throw new Error(errorData || 'Error al cargar las transacciones');
      }

      const data = await response.json();
      console.log('Response data:', data);

      // Transformar y validar los datos
      const transactions = (data.content || []).map((transaction: any) => ({
        codHistorialEstado: transaction.codHistorialEstado,
        codigoTransaccion: transaction.codigoTransaccion,
        estado: transaction.estado,
        fechaEstadoCambio: transaction.fechaEstadoCambio,
        detalle: transaction.detalle,
        transaccion: transaction.transaccion ? {
          codigo: transaction.transaccion.codigo,
          monto: transaction.transaccion.monto || 0,
          modalidad: transaction.transaccion.modalidad || '-',
          codigoMoneda: transaction.transaccion.codigoMoneda || 'USD',
          marca: transaction.transaccion.marca || '-',
          estado: transaction.transaccion.estado || '-',
          detalle: transaction.transaccion.detalle || '-',
          banco: transaction.transaccion.banco ? {
            codigo: transaction.transaccion.banco.codigo,
            nombreComercial: transaction.transaccion.banco.nombreComercial || '-',
            razonSocial: transaction.transaccion.banco.razonSocial || '-'
          } : null
        } : null
      }));

      set({ transactions, isLoading: false });
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