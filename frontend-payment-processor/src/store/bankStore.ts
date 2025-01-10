import { create } from 'zustand';
import axios from 'axios';

export interface Bank {
  codigo: number;
  codigoInterno: string;
  ruc: string;
  razonSocial: string;
  nombreComercial: string;
  fechaCreacion: string;
  comision: {
    codigo: number;
    nombre: string;
    porcentaje: number;
  };
  estado: string;
  fechaInactivacion?: string;
}

interface BankState {
  banks: Bank[];
  isLoading: boolean;
  error: string | null;
  fetchBanks: () => Promise<void>;
  searchBanks: (query: string) => Promise<void>;
  createBank: (bank: Omit<Bank, 'codigo' | 'fechaCreacion' | 'fechaInactivacion'>) => Promise<void>;
  updateBank: (id: number, bank: Partial<Bank>) => Promise<void>;
  deactivateBank: (id: number) => Promise<void>;
}

const API_URL = 'http://3.135.191.61/api/v1/bancos';

export const useBankStore = create<BankState>((set) => ({
  banks: [],
  isLoading: false,
  error: null,

  fetchBanks: async () => {
    try {
      set({ isLoading: true, error: null });
      const response = await axios.get(`${API_URL}/bancos-activos`);
      set({ banks: response.data, isLoading: false });
    } catch (error) {
      set({ 
        error: error instanceof Error ? error.message : 'Error al cargar los bancos', 
        isLoading: false 
      });
    }
  },

  searchBanks: async (query: string) => {
    try {
      set({ isLoading: true, error: null });
      const response = await axios.get(`${API_URL}/buscar-nombre?nombreComercial=${query}`);
      set({ banks: response.data, isLoading: false });
    } catch (error) {
      set({ 
        error: error instanceof Error ? error.message : 'Error al buscar bancos', 
        isLoading: false 
      });
    }
  },

  createBank: async (bank) => {
    try {
      set({ isLoading: true, error: null });
      const response = await axios.post(`${API_URL}/agregar-banco`, bank);
      set((state) => ({ 
        banks: [...state.banks, response.data],
        isLoading: false 
      }));
    } catch (error) {
      set({ 
        error: error instanceof Error ? error.message : 'Error al crear el banco', 
        isLoading: false 
      });
    }
  },

  updateBank: async (id, bank) => {
    try {
      set({ isLoading: true, error: null });
      const response = await axios.put(`${API_URL}/actualizar-banco/${id}`, bank);
      set((state) => ({
        banks: state.banks.map((b) => b.codigo === id ? response.data : b),
        isLoading: false
      }));
    } catch (error) {
      set({ 
        error: error instanceof Error ? error.message : 'Error al actualizar el banco', 
        isLoading: false 
      });
    }
  },

  deactivateBank: async (id) => {
    try {
      set({ isLoading: true, error: null });
      await axios.delete(`${API_URL}/inactivar-banco/${id}`);
      set((state) => ({
        banks: state.banks.filter((b) => b.codigo !== id),
        isLoading: false
      }));
    } catch (error) {
      set({ 
        error: error instanceof Error ? error.message : 'Error al desactivar el banco', 
        isLoading: false 
      });
    }
  },
})); 