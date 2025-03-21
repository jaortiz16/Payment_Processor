import { create } from 'zustand';
import axios from 'axios';

export interface FraudRule {
  codRegla: number;
  nombreRegla: string;
  descripcion?: string;
  tipoRegla: string;
  limiteTransacciones?: number;
  periodoTiempo: string;
  limiteMontoTotal?: number;
  puntajeRiesgo?: number;
  nivelRiesgo: string;
  estado: string;
  prioridad: number;
  fechaCreacion: string;
  fechaActualizacion: string;
}

interface FraudRuleState {
  rules: FraudRule[];
  isLoading: boolean;
  error: string | null;
  fetchRules: () => Promise<void>;
  createRule: (rule: Omit<FraudRule, 'codRegla' | 'fechaCreacion' | 'fechaActualizacion'>) => Promise<void>;
  updateRule: (id: number, rule: Partial<FraudRule>) => Promise<void>;
  deleteRule: (id: number) => Promise<void>;
}

const API_URL = 'http://18.117.166.56/v1/fraudes/reglas';

export const useFraudRuleStore = create<FraudRuleState>((set) => ({
  rules: [],
  isLoading: false,
  error: null,

  fetchRules: async () => {
    try {
      set({ isLoading: true, error: null });
      const response = await axios.get(API_URL);
      set({ rules: response.data, isLoading: false });
    } catch (error) {
      console.error('Error fetching rules:', error);
      set({ 
        error: error instanceof Error ? error.message : 'Error al cargar las reglas', 
        isLoading: false,
        rules: []
      });
    }
  },

  createRule: async (rule) => {
    try {
      set({ isLoading: true, error: null });
      const response = await axios.post(API_URL, rule);
      set((state) => ({ 
        rules: [...state.rules, response.data],
        isLoading: false 
      }));
    } catch (error) {
      console.error('Error creating rule:', error);
      set({ 
        error: error instanceof Error ? error.message : 'Error al crear la regla', 
        isLoading: false 
      });
      throw error;
    }
  },

  updateRule: async (id, rule) => {
    try {
      set({ isLoading: true, error: null });
      const response = await axios.put(`${API_URL}/${id}`, rule);
      set((state) => ({
        rules: state.rules.map((r) => r.codRegla === id ? response.data : r),
        isLoading: false
      }));
    } catch (error) {
      console.error('Error updating rule:', error);
      set({ 
        error: error instanceof Error ? error.message : 'Error al actualizar la regla', 
        isLoading: false 
      });
      throw error;
    }
  },

  deleteRule: async (id) => {
    try {
      set({ isLoading: true, error: null });
      await axios.delete(`${API_URL}/${id}`);
      set((state) => ({
        rules: state.rules.filter((r) => r.codRegla !== id),
        isLoading: false
      }));
    } catch (error) {
      console.error('Error deleting rule:', error);
      set({ 
        error: error instanceof Error ? error.message : 'Error al eliminar la regla', 
        isLoading: false 
      });
      throw error;
    }
  },
})); 