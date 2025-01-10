import { create } from 'zustand';
import axios from 'axios';

export interface CommissionSegment {
  pk: {
    codComision: number;
    codSegmento: number;
  };
  transaccionesHasta: number;
  monto: number;
}

export interface Commission {
  codigo: number;
  tipo: 'POR' | 'FIJ';
  montoBase: number;
  transaccionesBase: number;
  manejaSegmentos: boolean;
  segmentos?: CommissionSegment[];
}

interface CommissionState {
  commissions: Commission[];
  isLoading: boolean;
  error: string | null;
  fetchCommissionsByType: (tipo: string) => Promise<void>;
  fetchCommissionsByAmount: (montoMinimo: number, montoMaximo: number) => Promise<void>;
  createCommission: (commission: Omit<Commission, 'codigo'>) => Promise<void>;
  updateCommission: (id: number, commission: Partial<Commission>) => Promise<void>;
  addSegment: (commissionId: number, segment: Omit<CommissionSegment, 'pk'>) => Promise<void>;
}

const API_URL = 'http://3.14.250.222/api/v1/comisiones';

export const useCommissionStore = create<CommissionState>((set) => ({
  commissions: [],
  isLoading: false,
  error: null,

  fetchCommissionsByType: async (tipo: string) => {
    try {
      set({ isLoading: true, error: null });
      const response = await axios.get(`${API_URL}/tipo-comision/${tipo}`);
      set({ commissions: response.data, isLoading: false });
    } catch (error) {
      set({ 
        error: error instanceof Error ? error.message : 'Error al cargar las comisiones', 
        isLoading: false 
      });
    }
  },

  fetchCommissionsByAmount: async (montoMinimo: number, montoMaximo: number) => {
    try {
      set({ isLoading: true, error: null });
      const response = await axios.get(`${API_URL}/monto-comision`, {
        params: { montoMinimo, montoMaximo }
      });
      set({ commissions: response.data, isLoading: false });
    } catch (error) {
      set({ 
        error: error instanceof Error ? error.message : 'Error al buscar comisiones', 
        isLoading: false 
      });
    }
  },

  createCommission: async (commission) => {
    try {
      set({ isLoading: true, error: null });
      const response = await axios.post(`${API_URL}/crear-comision`, commission);
      set((state) => ({ 
        commissions: [...state.commissions, response.data],
        isLoading: false 
      }));
    } catch (error) {
      set({ 
        error: error instanceof Error ? error.message : 'Error al crear la comisión', 
        isLoading: false 
      });
    }
  },

  updateCommission: async (id, commission) => {
    try {
      set({ isLoading: true, error: null });
      const response = await axios.put(`${API_URL}/actualizar-comision/${id}`, commission);
      set((state) => ({
        commissions: state.commissions.map((c) => c.codigo === id ? response.data : c),
        isLoading: false
      }));
    } catch (error) {
      set({ 
        error: error instanceof Error ? error.message : 'Error al actualizar la comisión', 
        isLoading: false 
      });
    }
  },

  addSegment: async (commissionId, segment) => {
    try {
      set({ isLoading: true, error: null });
      const response = await axios.post(`${API_URL}/${commissionId}/segmentos`, segment);
      set((state) => ({
        commissions: state.commissions.map((c) => {
          if (c.codigo === commissionId) {
            return {
              ...c,
              segmentos: [...(c.segmentos || []), response.data]
            };
          }
          return c;
        }),
        isLoading: false
      }));
    } catch (error) {
      set({ 
        error: error instanceof Error ? error.message : 'Error al agregar el segmento', 
        isLoading: false 
      });
    }
  },
})); 