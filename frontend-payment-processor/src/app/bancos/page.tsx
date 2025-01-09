'use client';

import { useEffect, useState } from 'react';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Building2, Search, Plus, Loader2, AlertTriangle } from "lucide-react";
import { useBankStore } from '@/store/bankStore';
import { format } from 'date-fns';
import { es } from 'date-fns/locale';

interface BankFormData {
  codigoInterno: string;
  ruc: string;
  razonSocial: string;
  nombreComercial: string;
  estado: string;
  comision: {
    codigo: number;
    nombre: string;
    porcentaje: number;
  };
}

const initialFormData: BankFormData = {
  codigoInterno: '',
  ruc: '',
  razonSocial: '',
  nombreComercial: '',
  estado: 'ACT',
  comision: {
    codigo: 1,
    nombre: 'Comisión Estándar',
    porcentaje: 0.5
  }
};

export default function BanksPage() {
  const { banks, isLoading, error, fetchBanks, searchBanks, createBank, updateBank, deactivateBank } = useBankStore();
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [formData, setFormData] = useState<BankFormData>(initialFormData);
  const [selectedBank, setSelectedBank] = useState<number | null>(null);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchBanks();
  }, [fetchBanks]);

  const handleSearch = (value: string) => {
    setSearchTerm(value);
    if (value.trim()) {
      searchBanks(value);
    } else {
      fetchBanks();
    }
  };

  const handleCreateSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await createBank(formData);
    setIsCreateOpen(false);
    setFormData(initialFormData);
  };

  const handleEditSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (selectedBank !== null) {
      await updateBank(selectedBank, formData);
      setIsEditOpen(false);
      setSelectedBank(null);
      setFormData(initialFormData);
    }
  };

  const handleEdit = (bank: any) => {
    setSelectedBank(bank.codigo);
    setFormData({
      codigoInterno: bank.codigoInterno,
      ruc: bank.ruc,
      razonSocial: bank.razonSocial,
      nombreComercial: bank.nombreComercial,
      estado: bank.estado,
      comision: {
        codigo: bank.comision.codigo,
        nombre: bank.comision.nombre,
        porcentaje: bank.comision.porcentaje
      }
    });
    setIsEditOpen(true);
  };

  const handleDeactivateBank = async (id: number) => {
    if (window.confirm('¿Estás seguro de que deseas desactivar este banco?')) {
      await deactivateBank(id);
    }
  };

  const BankForm = ({ onSubmit, isEdit = false }: { onSubmit: (e: React.FormEvent) => Promise<void>, isEdit?: boolean }) => (
    <form onSubmit={onSubmit} className="space-y-4">
      <div className="grid grid-cols-2 gap-4">
        <div className="space-y-2">
          <Label htmlFor="codigoInterno">Código Interno</Label>
          <Input
            id="codigoInterno"
            value={formData.codigoInterno}
            onChange={(e) => setFormData({ ...formData, codigoInterno: e.target.value })}
            maxLength={10}
            required
          />
        </div>
        <div className="space-y-2">
          <Label htmlFor="ruc">RUC</Label>
          <Input
            id="ruc"
            value={formData.ruc}
            onChange={(e) => setFormData({ ...formData, ruc: e.target.value })}
            maxLength={13}
            pattern="\\d{13}"
            required
          />
        </div>
      </div>

      <div className="space-y-2">
        <Label htmlFor="razonSocial">Razón Social</Label>
        <Input
          id="razonSocial"
          value={formData.razonSocial}
          onChange={(e) => setFormData({ ...formData, razonSocial: e.target.value })}
          minLength={5}
          required
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="nombreComercial">Nombre Comercial</Label>
        <Input
          id="nombreComercial"
          value={formData.nombreComercial}
          onChange={(e) => setFormData({ ...formData, nombreComercial: e.target.value })}
          minLength={3}
          required
        />
      </div>

      <DialogFooter>
        <Button type="submit">{isEdit ? 'Actualizar' : 'Crear'} Banco</Button>
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
        <h2 className="text-3xl font-bold tracking-tight">Bancos</h2>
        <Button onClick={() => setIsCreateOpen(true)}>
          <Plus className="mr-2 h-4 w-4" /> Nuevo Banco
        </Button>
      </div>

      <div className="flex items-center space-x-2">
        <div className="relative flex-1 max-w-sm">
          <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
          <Input
            placeholder="Buscar por nombre..."
            className="pl-8"
            value={searchTerm}
            onChange={(e) => handleSearch(e.target.value)}
          />
        </div>
      </div>

      <Dialog open={isCreateOpen} onOpenChange={setIsCreateOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Crear Nuevo Banco</DialogTitle>
            <DialogDescription>
              Complete el formulario para crear un nuevo banco.
            </DialogDescription>
          </DialogHeader>
          <BankForm onSubmit={handleCreateSubmit} />
        </DialogContent>
      </Dialog>

      <Dialog open={isEditOpen} onOpenChange={setIsEditOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Editar Banco</DialogTitle>
            <DialogDescription>
              Modifique los campos que desea actualizar.
            </DialogDescription>
          </DialogHeader>
          <BankForm onSubmit={handleEditSubmit} isEdit />
        </DialogContent>
      </Dialog>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {banks.map((bank) => (
          <Card key={bank.codigo} className="flex flex-col">
            <CardHeader>
              <div className="flex items-start gap-4">
                <div className="rounded-lg p-2 bg-primary/10">
                  <Building2 className="h-6 w-6 text-primary" />
                </div>
                <div>
                  <CardTitle className="text-xl">{bank.nombreComercial}</CardTitle>
                  <CardDescription>{bank.razonSocial}</CardDescription>
                </div>
              </div>
            </CardHeader>
            <CardContent className="flex-grow">
              <div className="space-y-2">
                <div className="flex justify-between text-sm">
                  <span className="text-muted-foreground">RUC:</span>
                  <span>{bank.ruc}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-muted-foreground">Código Interno:</span>
                  <span>{bank.codigoInterno}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-muted-foreground">Comisión:</span>
                  <span>{bank.comision.porcentaje}%</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-muted-foreground">Fecha Creación:</span>
                  <span>{format(new Date(bank.fechaCreacion), 'dd/MM/yyyy', { locale: es })}</span>
                </div>
              </div>
            </CardContent>
            <CardFooter className="flex justify-end gap-2">
              <Button 
                variant="outline" 
                size="sm"
                onClick={() => handleEdit(bank)}
              >
                Editar
              </Button>
              <Button 
                variant="destructive" 
                size="sm"
                onClick={() => handleDeactivateBank(bank.codigo)}
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