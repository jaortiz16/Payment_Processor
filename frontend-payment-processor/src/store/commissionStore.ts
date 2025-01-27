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
  fechaCreacion: string;
  segmentos?: CommissionSegment[];
}

interface CommissionState {
  commissions: Commission[];
  isLoading: boolean;
  error: string | null;
  fetchCommissionsByType: (tipo: string) => Promise<void>;
  fetchCommissionsByAmount: (montoMinimo: number, montoMaximo: number) => Promise<void>;
  createCommission: (commission: Omit<Commission, 'codigo' | 'fechaCreacion'>) => Promise<void>;
  updateCommission: (id: number, commission: Partial<Commission>) => Promise<void>;
  addSegment: (commissionId: number, segment: Omit<CommissionSegment, 'pk'>) => Promise<void>;
}

const API_URL = 'http://3.144.95.97/v1/comisiones';

export const useCommissionStore = create<CommissionState>((set) => ({
  commissions: [],
  isLoading: false,
  error: null,

  fetchCommissionsByType: async (tipo: string) => {
    try {
      set({ isLoading: true, error: null });
      const response = await axios.get(API_URL, {
        params: { tipo },
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        }
      });
      set({ commissions: response.data, isLoading: false });
    } catch (error) {
      console.error('Error fetching commissions:', error);
      set({ 
        error: error instanceof Error ? error.message : 'Error al cargar las comisiones', 
        isLoading: false,
        commissions: []
      });
    }
  },

  fetchCommissionsByAmount: async (montoMinimo: number, montoMaximo: number) => {
    try {
      set({ isLoading: true, error: null });
      const response = await axios.get(API_URL, {
        params: { montoMinimo, montoMaximo },
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        }
      });
      set({ commissions: response.data, isLoading: false });
    } catch (error) {
      console.error('Error fetching commissions by amount:', error);
      set({ 
        error: error instanceof Error ? error.message : 'Error al buscar comisiones', 
        isLoading: false,
        commissions: []
      });
    }
  },

  createCommission: async (commission) => {
    try {
      set({ isLoading: true, error: null });
      const response = await axios.post(API_URL, commission, {
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        }
      });
      set((state) => ({ 
        commissions: [...state.commissions, response.data],
        isLoading: false 
      }));
    } catch (error) {
      console.error('Error creating commission:', error);
      set({ 
        error: error instanceof Error ? error.message : 'Error al crear la comisión', 
        isLoading: false 
      });
      throw error;
    }
  },

  updateCommission: async (id, commission) => {
    try {
      set({ isLoading: true, error: null });
      const response = await axios.put(`${API_URL}/${id}`, commission, {
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        }
      });
      set((state) => ({
        commissions: state.commissions.map((c) => c.codigo === id ? response.data : c),
        isLoading: false
      }));
    } catch (error) {
      console.error('Error updating commission:', error);
      set({ 
        error: error instanceof Error ? error.message : 'Error al actualizar la comisión', 
        isLoading: false 
      });
      throw error;
    }
  },

  addSegment: async (commissionId, segment) => {
    try {
      set({ isLoading: true, error: null });
      const response = await axios.post(`${API_URL}/${commissionId}/segmentos`, segment, {
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        }
      });
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
      console.error('Error adding segment:', error);
      set({ 
        error: error instanceof Error ? error.message : 'Error al agregar el segmento', 
        isLoading: false 
      });
      throw error;
    }
  },
})); 