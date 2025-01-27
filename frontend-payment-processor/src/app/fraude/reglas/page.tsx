'use client';

import { useEffect, useState } from 'react';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Plus, Loader2, AlertTriangle } from "lucide-react";
import { useFraudRuleStore } from '@/store/fraudRuleStore';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";

const tipoReglaText: Record<string, string> = {
  TRX: "Transacciones",
  MNT: "Monto",
  UBI: "Ubicación"
};

const nivelRiesgoStyles: Record<string, string> = {
  BAJ: "bg-green-600 text-white",
  MED: "bg-yellow-600 text-white",
  ALT: "bg-red-600 text-white"
};

const nivelRiesgoText: Record<string, string> = {
  BAJ: "Bajo",
  MED: "Medio",
  ALT: "Alto"
};

const periodoTiempoText: Record<string, string> = {
  HOR: "Por Hora",
  DIA: "Por Día",
  SEM: "Por Semana"
};

interface RuleFormData {
  nombreRegla: string;
  descripcion: string;
  tipoRegla: string;
  limiteTransacciones?: number;
  periodoTiempo: string;
  limiteMontoTotal?: number;
  puntajeRiesgo: number;
  nivelRiesgo: string;
  prioridad: number;
  estado: string;
}

const initialFormData: RuleFormData = {
  nombreRegla: '',
  descripcion: '',
  tipoRegla: 'TRX',
  periodoTiempo: 'DIA',
  puntajeRiesgo: 50,
  nivelRiesgo: 'MED',
  prioridad: 1,
  estado: 'ACT'
};

export default function FraudRulesPage() {
  const { rules, isLoading, error, fetchRules, createRule, updateRule, deleteRule } = useFraudRuleStore();
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [formData, setFormData] = useState<RuleFormData>(initialFormData);
  const [selectedRule, setSelectedRule] = useState<number | null>(null);

  useEffect(() => {
    fetchRules();
  }, [fetchRules]);

  const handleCreateSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await createRule(formData);
    setIsCreateOpen(false);
    setFormData(initialFormData);
  };

  const handleEditSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (selectedRule !== null) {
      await updateRule(selectedRule, formData);
      setIsEditOpen(false);
      setSelectedRule(null);
      setFormData(initialFormData);
    }
  };

  const handleEdit = (rule: any) => {
    setSelectedRule(rule.codRegla);
    setFormData({
      nombreRegla: rule.nombreRegla,
      descripcion: rule.descripcion,
      tipoRegla: rule.tipoRegla,
      limiteTransacciones: rule.limiteTransacciones,
      periodoTiempo: rule.periodoTiempo,
      limiteMontoTotal: rule.limiteMontoTotal,
      puntajeRiesgo: rule.puntajeRiesgo,
      nivelRiesgo: rule.nivelRiesgo,
      prioridad: rule.prioridad,
      estado: rule.estado
    });
    setIsEditOpen(true);
  };

  const handleDeleteRule = async (id: number) => {
    if (window.confirm('¿Estás seguro de que deseas desactivar esta regla?')) {
      await deleteRule(id);
    }
  };

  const RuleForm = ({ onSubmit, isEdit = false }: { onSubmit: (e: React.FormEvent) => Promise<void>, isEdit?: boolean }) => (
    <form onSubmit={onSubmit} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="nombreRegla">Nombre de la Regla</Label>
        <Input
          id="nombreRegla"
          value={formData.nombreRegla}
          onChange={(e) => setFormData({ ...formData, nombreRegla: e.target.value })}
          required
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="descripcion">Descripción</Label>
        <Input
          id="descripcion"
          value={formData.descripcion}
          onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })}
        />
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div className="space-y-2">
          <Label htmlFor="tipoRegla">Tipo de Regla</Label>
          <Select
            value={formData.tipoRegla}
            onValueChange={(value) => setFormData({ ...formData, tipoRegla: value })}
          >
            <SelectTrigger>
              <SelectValue placeholder="Seleccionar tipo" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="TRX">Transacciones</SelectItem>
              <SelectItem value="MNT">Monto</SelectItem>
              <SelectItem value="UBI">Ubicación</SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div className="space-y-2">
          <Label htmlFor="periodoTiempo">Periodo de Tiempo</Label>
          <Select
            value={formData.periodoTiempo}
            onValueChange={(value) => setFormData({ ...formData, periodoTiempo: value })}
          >
            <SelectTrigger>
              <SelectValue placeholder="Seleccionar periodo" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="HOR">Por Hora</SelectItem>
              <SelectItem value="DIA">Por Día</SelectItem>
              <SelectItem value="SEM">Por Semana</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </div>

      {formData.tipoRegla === 'TRX' && (
        <div className="space-y-2">
          <Label htmlFor="limiteTransacciones">Límite de Transacciones</Label>
          <Input
            id="limiteTransacciones"
            type="number"
            value={formData.limiteTransacciones || ''}
            onChange={(e) => setFormData({ ...formData, limiteTransacciones: Number(e.target.value) })}
            required
          />
        </div>
      )}

      {formData.tipoRegla === 'MNT' && (
        <div className="space-y-2">
          <Label htmlFor="limiteMontoTotal">Límite de Monto</Label>
          <Input
            id="limiteMontoTotal"
            type="number"
            step="0.01"
            value={formData.limiteMontoTotal || ''}
            onChange={(e) => setFormData({ ...formData, limiteMontoTotal: Number(e.target.value) })}
            required
          />
        </div>
      )}

      <div className="grid grid-cols-2 gap-4">
        <div className="space-y-2">
          <Label htmlFor="puntajeRiesgo">Puntaje de Riesgo</Label>
          <Input
            id="puntajeRiesgo"
            type="number"
            min="0"
            max="100"
            value={formData.puntajeRiesgo}
            onChange={(e) => setFormData({ ...formData, puntajeRiesgo: Number(e.target.value) })}
            required
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="nivelRiesgo">Nivel de Riesgo</Label>
          <Select
            value={formData.nivelRiesgo}
            onValueChange={(value) => setFormData({ ...formData, nivelRiesgo: value })}
          >
            <SelectTrigger>
              <SelectValue placeholder="Seleccionar nivel" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="BAJ">Bajo</SelectItem>
              <SelectItem value="MED">Medio</SelectItem>
              <SelectItem value="ALT">Alto</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </div>

      <div className="space-y-2">
        <Label htmlFor="prioridad">Prioridad</Label>
        <Input
          id="prioridad"
          type="number"
          min="1"
          value={formData.prioridad}
          onChange={(e) => setFormData({ ...formData, prioridad: Number(e.target.value) })}
          required
        />
      </div>

      <DialogFooter>
        <Button type="submit">{isEdit ? 'Actualizar' : 'Crear'} Regla</Button>
      </DialogFooter>
    </form>
  );

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen gap-4">
        <AlertTriangle className="h-12 w-12 text-red-500" />
        <p className="text-red-500">{error}</p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h2 className="text-3xl font-bold tracking-tight">Reglas de Fraude</h2>
        <Button onClick={() => setIsCreateOpen(true)}>
          <Plus className="mr-2 h-4 w-4" /> Nueva Regla
        </Button>
      </div>

      <Dialog open={isCreateOpen} onOpenChange={setIsCreateOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Crear Nueva Regla</DialogTitle>
            <DialogDescription>
              Complete el formulario para crear una nueva regla de fraude.
            </DialogDescription>
          </DialogHeader>
          <RuleForm onSubmit={handleCreateSubmit} />
        </DialogContent>
      </Dialog>

      <Dialog open={isEditOpen} onOpenChange={setIsEditOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Editar Regla</DialogTitle>
            <DialogDescription>
              Modifique los campos que desea actualizar.
            </DialogDescription>
          </DialogHeader>
          <RuleForm onSubmit={handleEditSubmit} isEdit />
        </DialogContent>
      </Dialog>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {rules.map((rule) => (
          <Card key={rule.codRegla} className="flex flex-col">
            <CardHeader>
              <div className="flex justify-between items-start">
                <div>
                  <CardTitle className="text-xl">{rule.nombreRegla}</CardTitle>
                  <CardDescription>
                    {tipoReglaText[rule.tipoRegla]} - {periodoTiempoText[rule.periodoTiempo]}
                  </CardDescription>
                </div>
                <Badge variant="outline" className={nivelRiesgoStyles[rule.nivelRiesgo]}>
                  {nivelRiesgoText[rule.nivelRiesgo]}
                </Badge>
              </div>
            </CardHeader>
            <CardContent className="flex-grow">
              <div className="space-y-2">
                {rule.tipoRegla === 'TRX' && rule.limiteTransacciones && (
                  <p>Límite: {rule.limiteTransacciones} transacciones</p>
                )}
                {rule.tipoRegla === 'MNT' && rule.limiteMontoTotal && (
                  <p>Límite: {new Intl.NumberFormat('es-EC', {
                    style: 'currency',
                    currency: 'USD'
                  }).format(rule.limiteMontoTotal)}</p>
                )}
                {rule.tipoRegla === 'UBI' && (
                  <p>Detecta transacciones en diferentes países</p>
                )}
                <p className="text-sm text-muted-foreground">
                  Prioridad: {rule.prioridad}
                </p>
              </div>
            </CardContent>
            <CardFooter className="flex justify-end gap-2">
              <Button 
                variant="outline" 
                size="sm"
                onClick={() => handleEdit(rule)}
              >
                Editar
              </Button>
              <Button 
                variant="destructive" 
                size="sm"
                onClick={() => handleDeleteRule(rule.codRegla)}
              >
                Desactivar
              </Button>
            </CardFooter>
          </Card>
        ))}
      </div>
    </div>
  );
} 