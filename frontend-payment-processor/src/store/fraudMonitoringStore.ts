import { create } from 'zustand';
import axios from 'axios';

export interface FraudMonitoring {
  codigo: number;
  reglaFraude: {
    codigo: number;
    nombre: string;
    tipoRegla: string;
  };
  transaccion: {
    codigo: number;
    codigoUnicoTransaccion: string;
    monto: number;
    numeroTarjeta: string;
  };
  nivelRiesgo: string;
  puntajeRiesgo: number;
  estado: string;
  detalle: string;
  fechaDeteccion: string;
  fechaProcesamiento?: string;
  codigoUnicoTransaccion: string;
}

interface FraudMonitoringState {
  alerts: FraudMonitoring[];
  isLoading: boolean;
  error: string | null;
  fetchPendingAlerts: () => Promise<void>;
  fetchAlertsByDate: (fechaInicio: string, fechaFin: string) => Promise<void>;
  fetchAlertsByTransaction: (codTransaccion: number) => Promise<void>;
  processAlert: (id: number, estado: string, detalle?: string) => Promise<void>;
}

const API_URL = 'http://3.144.95.97/api/v1/monitoreo-fraude';

export const useFraudMonitoringStore = create<FraudMonitoringState>((set) => ({
  alerts: [],
  isLoading: false,
  error: null,

  fetchPendingAlerts: async () => {
    try {
      set({ isLoading: true, error: null });
      const response = await axios.get(`${API_URL}/alertas/pendientes`);
      set({ alerts: response.data, isLoading: false });
    } catch (error) {
      set({ 
        error: error instanceof Error ? error.message : 'Error al cargar las alertas pendientes', 
        isLoading: false 
      });
    }
  },

  fetchAlertsByDate: async (fechaInicio: string, fechaFin: string) => {
    try {
      set({ isLoading: true, error: null });
      const response = await axios.get(`${API_URL}/alertas/por-fecha`, {
        params: { fechaInicio, fechaFin }
      });
      set({ alerts: response.data, isLoading: false });
    } catch (error) {
      set({ 
        error: error instanceof Error ? error.message : 'Error al buscar alertas por fecha', 
        isLoading: false 
      });
    }
  },

  fetchAlertsByTransaction: async (codTransaccion: number) => {
    try {
      set({ isLoading: true, error: null });
      const response = await axios.get(`${API_URL}/alertas/por-transaccion/${codTransaccion}`);
      set({ alerts: response.data, isLoading: false });
    } catch (error) {
      set({ 
        error: error instanceof Error ? error.message : 'Error al buscar alertas por transacción', 
        isLoading: false 
      });
    }
  },

  processAlert: async (id: number, estado: string, detalle?: string) => {
    try {
      set({ isLoading: true, error: null });
      await axios.put(`${API_URL}/alertas/${id}/procesar`, null, {
        params: { estado, detalle }
      });
      set((state) => ({
        alerts: state.alerts.filter(alert => alert.codigo !== id),
        isLoading: false
      }));
    } catch (error) {
      set({ 
        error: error instanceof Error ? error.message : 'Error al procesar la alerta', 
        isLoading: false 
      });
    }
  },
})); 