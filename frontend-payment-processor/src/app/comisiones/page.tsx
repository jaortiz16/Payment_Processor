'use client';

import { useEffect, useState } from 'react';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Switch } from "@/components/ui/switch";
import { Percent, DollarSign, Loader2, AlertTriangle, Plus } from "lucide-react";
import { useCommissionStore } from '@/store/commissionStore';

interface CommissionFormData {
  tipo: 'POR' | 'FIJ';
  montoBase: number;
  transaccionesBase: number;
  manejaSegmentos: boolean;
}

const initialFormData: CommissionFormData = {
  tipo: 'POR',
  montoBase: 0,
  transaccionesBase: 0,
  manejaSegmentos: false,
};

interface SegmentFormData {
  transaccionesHasta: number;
  monto: number;
}

const initialSegmentFormData: SegmentFormData = {
  transaccionesHasta: 0,
  monto: 0,
};

export default function CommissionsPage() {
  const { commissions, isLoading, error, fetchCommissionsByType, createCommission, updateCommission, addSegment } = useCommissionStore();
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [isAddSegmentOpen, setIsAddSegmentOpen] = useState(false);
  const [formData, setFormData] = useState<CommissionFormData>(initialFormData);
  const [segmentFormData, setSegmentFormData] = useState<SegmentFormData>(initialSegmentFormData);
  const [selectedCommission, setSelectedCommission] = useState<number | null>(null);
  const [selectedType, setSelectedType] = useState<'POR' | 'FIJ'>('POR');

  useEffect(() => {
    fetchCommissionsByType(selectedType);
  }, [fetchCommissionsByType, selectedType]);

  const handleCreateSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await createCommission(formData);
    setIsCreateOpen(false);
    setFormData(initialFormData);
  };

  const handleEditSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (selectedCommission !== null) {
      await updateCommission(selectedCommission, formData);
      setIsEditOpen(false);
      setSelectedCommission(null);
      setFormData(initialFormData);
    }
  };

  const handleAddSegmentSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (selectedCommission !== null) {
      await addSegment(selectedCommission, segmentFormData);
      setIsAddSegmentOpen(false);
      setSelectedCommission(null);
      setSegmentFormData(initialSegmentFormData);
    }
  };

  const handleEdit = (commission: any) => {
    setSelectedCommission(commission.codigo);
    setFormData({
      tipo: commission.tipo,
      montoBase: commission.montoBase,
      transaccionesBase: commission.transaccionesBase,
      manejaSegmentos: commission.manejaSegmentos,
    });
    setIsEditOpen(true);
  };

  const handleAddSegment = (commission: any) => {
    setSelectedCommission(commission.codigo);
    setIsAddSegmentOpen(true);
  };

  const CommissionForm = ({ onSubmit, isEdit = false }: { onSubmit: (e: React.FormEvent) => Promise<void>, isEdit?: boolean }) => (
    <form onSubmit={onSubmit} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="tipo">Tipo de Comisión</Label>
        <Select
          value={formData.tipo}
          onValueChange={(value: 'POR' | 'FIJ') => setFormData({ ...formData, tipo: value })}
        >
          <SelectTrigger>
            <SelectValue placeholder="Seleccione el tipo" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="POR">Porcentaje</SelectItem>
            <SelectItem value="FIJ">Fijo</SelectItem>
          </SelectContent>
        </Select>
      </div>

      <div className="space-y-2">
        <Label htmlFor="montoBase">Monto Base</Label>
        <Input
          id="montoBase"
          type="number"
          step="0.01"
          value={formData.montoBase}
          onChange={(e) => setFormData({ ...formData, montoBase: parseFloat(e.target.value) })}
          min="0"
          required
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="transaccionesBase">Transacciones Base</Label>
        <Input
          id="transaccionesBase"
          type="number"
          value={formData.transaccionesBase}
          onChange={(e) => setFormData({ ...formData, transaccionesBase: parseInt(e.target.value) })}
          min="0"
          required
        />
      </div>

      <div className="flex items-center space-x-2">
        <Switch
          id="manejaSegmentos"
          checked={formData.manejaSegmentos}
          onCheckedChange={(checked) => setFormData({ ...formData, manejaSegmentos: checked })}
        />
        <Label htmlFor="manejaSegmentos">Maneja Segmentos</Label>
      </div>

      <DialogFooter>
        <Button type="submit">{isEdit ? 'Actualizar' : 'Crear'} Comisión</Button>
      </DialogFooter>
    </form>
  );

  const SegmentForm = ({ onSubmit }: { onSubmit: (e: React.FormEvent) => Promise<void> }) => (
    <form onSubmit={onSubmit} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="transaccionesHasta">Transacciones Hasta</Label>
        <Input
          id="transaccionesHasta"
          type="number"
          value={segmentFormData.transaccionesHasta}
          onChange={(e) => setSegmentFormData({ ...segmentFormData, transaccionesHasta: parseInt(e.target.value) })}
          min="0"
          required
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="monto">Monto</Label>
        <Input
          id="monto"
          type="number"
          step="0.01"
          value={segmentFormData.monto}
          onChange={(e) => setSegmentFormData({ ...segmentFormData, monto: parseFloat(e.target.value) })}
          min="0"
          required
        />
      </div>

      <DialogFooter>
        <Button type="submit">Agregar Segmento</Button>
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
        <h2 className="text-3xl font-bold tracking-tight">Comisiones</h2>
        <Button onClick={() => setIsCreateOpen(true)}>
          <Plus className="mr-2 h-4 w-4" /> Nueva Comisión
        </Button>
      </div>

      <div className="flex items-center space-x-2">
        <Select value={selectedType} onValueChange={(value: 'POR' | 'FIJ') => setSelectedType(value)}>
          <SelectTrigger className="w-[180px]">
            <SelectValue placeholder="Tipo de comisión" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="POR">Porcentaje</SelectItem>
            <SelectItem value="FIJ">Fijo</SelectItem>
          </SelectContent>
        </Select>
      </div>

      <Dialog open={isCreateOpen} onOpenChange={setIsCreateOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Crear Nueva Comisión</DialogTitle>
            <DialogDescription>
              Complete el formulario para crear una nueva comisión.
            </DialogDescription>
          </DialogHeader>
          <CommissionForm onSubmit={handleCreateSubmit} />
        </DialogContent>
      </Dialog>

      <Dialog open={isEditOpen} onOpenChange={setIsEditOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Editar Comisión</DialogTitle>
            <DialogDescription>
              Modifique los campos que desea actualizar.
            </DialogDescription>
          </DialogHeader>
          <CommissionForm onSubmit={handleEditSubmit} isEdit />
        </DialogContent>
      </Dialog>

      <Dialog open={isAddSegmentOpen} onOpenChange={setIsAddSegmentOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Agregar Segmento</DialogTitle>
            <DialogDescription>
              Complete el formulario para agregar un nuevo segmento.
            </DialogDescription>
          </DialogHeader>
          <SegmentForm onSubmit={handleAddSegmentSubmit} />
        </DialogContent>
      </Dialog>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {commissions.map((commission) => (
          <Card key={commission.codigo} className="flex flex-col">
            <CardHeader>
              <div className="flex items-start gap-4">
                <div className="rounded-lg p-2 bg-primary/10">
                  {commission.tipo === 'POR' ? (
                    <Percent className="h-6 w-6 text-primary" />
                  ) : (
                    <DollarSign className="h-6 w-6 text-primary" />
                  )}
                </div>
                <div>
                  <CardTitle className="text-xl">
                    {commission.tipo === 'POR' ? 'Porcentaje' : 'Fijo'}
                  </CardTitle>
                  <CardDescription>
                    {commission.tipo === 'POR' ? `${commission.montoBase}%` : `$${commission.montoBase}`}
                  </CardDescription>
                </div>
              </div>
            </CardHeader>
            <CardContent className="flex-grow">
              <div className="space-y-2">
                <div className="flex justify-between text-sm">
                  <span className="text-muted-foreground">Transacciones Base:</span>
                  <span>{commission.transaccionesBase}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-muted-foreground">Maneja Segmentos:</span>
                  <span>{commission.manejaSegmentos ? 'Sí' : 'No'}</span>
                </div>
                {commission.segmentos && commission.segmentos.length > 0 && (
                  <div className="mt-4">
                    <h4 className="text-sm font-medium mb-2">Segmentos:</h4>
                    <div className="space-y-2">
                      {commission.segmentos.map((segment) => (
                        <div key={segment.pk.codSegmento} className="text-sm">
                          <div className="flex justify-between">
                            <span>Hasta {segment.transaccionesHasta} trans.</span>
                            <span>{commission.tipo === 'POR' ? `${segment.monto}%` : `$${segment.monto}`}</span>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            </CardContent>
            <CardFooter className="flex justify-end gap-2">
              <Button 
                variant="outline" 
                size="sm"
                onClick={() => handleEdit(commission)}
              >
                Editar
              </Button>
              {commission.manejaSegmentos && (
                <Button 
                  variant="outline" 
                  size="sm"
                  onClick={() => handleAddSegment(commission)}
                >
                  Agregar Segmento
                </Button>
              )}
            </CardFooter>
          </Card>
        ))}
      </div>
    </div>
  );
} 