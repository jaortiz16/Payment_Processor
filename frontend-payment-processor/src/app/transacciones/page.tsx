'use client';

import { useEffect, useState } from 'react';
import { useTransactionStore } from '@/store/transactionStore';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";

export default function TransactionsPage() {
  const { transactions, fetchTransactions, isLoading, error } = useTransactionStore();
  const [estado, setEstado] = useState('ALL');
  const [fechaInicio, setFechaInicio] = useState('');
  const [fechaFin, setFechaFin] = useState('');
  const [bancoNombre, setBancoNombre] = useState('');

  useEffect(() => {
    fetchTransactions({});
  }, []);

  const handleSearch = () => {
    fetchTransactions({
      estado: estado === 'ALL' ? undefined : estado,
      fechaInicio,
      fechaFin,
      bancoNombre: bancoNombre || undefined
    });
  };

  const getEstadoBadge = (estado: string) => {
    const styles = {
      PEN: 'bg-yellow-500 hover:bg-yellow-600',
      APR: 'bg-green-500 hover:bg-green-600',
      REC: 'bg-red-500 hover:bg-red-600',
      REV: 'bg-blue-500 hover:bg-blue-600',
      PRO: 'bg-purple-500 hover:bg-purple-600'
    };
    
    const labels = {
      PEN: 'Pendiente',
      APR: 'Aprobada',
      REC: 'Rechazada',
      REV: 'En Revisión',
      PRO: 'Procesada'
    };

    return (
      <Badge className={styles[estado as keyof typeof styles] || 'bg-gray-500'}>
        {labels[estado as keyof typeof labels] || estado}
      </Badge>
    );
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('es-EC', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  };

  return (
    <div className="container mx-auto py-10">
      <h1 className="text-2xl font-bold mb-6">Transacciones</h1>
      
      <div className="flex gap-4 mb-6">
        <Select value={estado} onValueChange={setEstado}>
          <SelectTrigger className="w-[180px]">
            <SelectValue placeholder="Todos los estados" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="ALL">Todos</SelectItem>
            <SelectItem value="PEN">Pendiente</SelectItem>
            <SelectItem value="APR">Aprobada</SelectItem>
            <SelectItem value="REC">Rechazada</SelectItem>
            <SelectItem value="REV">En Revisión</SelectItem>
            <SelectItem value="PRO">Procesada</SelectItem>
          </SelectContent>
        </Select>

        <Input
          type="datetime-local"
          value={fechaInicio}
          onChange={(e) => setFechaInicio(e.target.value)}
          className="w-[200px]"
        />

        <Input
          type="datetime-local"
          value={fechaFin}
          onChange={(e) => setFechaFin(e.target.value)}
          className="w-[200px]"
        />

        <Input
          placeholder="Nombre del Banco"
          value={bancoNombre}
          onChange={(e) => setBancoNombre(e.target.value)}
          className="w-[200px]"
        />

        <Button onClick={handleSearch}>Buscar</Button>
      </div>

      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
      )}

      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>ID</TableHead>
            <TableHead>Banco</TableHead>
            <TableHead>Monto</TableHead>
            <TableHead>Estado</TableHead>
            <TableHead>Modalidad</TableHead>
            <TableHead>Fecha Cambio</TableHead>
            <TableHead>Detalle</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {isLoading ? (
            <TableRow>
              <TableCell colSpan={7} className="text-center">Cargando...</TableCell>
            </TableRow>
          ) : transactions.length === 0 ? (
            <TableRow>
              <TableCell colSpan={7} className="text-center">No hay transacciones</TableCell>
            </TableRow>
          ) : (
            transactions.map((transaction) => (
              <TableRow key={transaction.codHistorialEstado}>
                <TableCell>{transaction.codigoTransaccion}</TableCell>
                <TableCell>
                  {transaction.transaccion?.banco?.nombreComercial || 
                   transaction.transaccion?.banco?.razonSocial || '-'}
                </TableCell>
                <TableCell>
                  {transaction.transaccion?.monto ? 
                    formatCurrency(transaction.transaccion.monto) : '-'}
                </TableCell>
                <TableCell>{getEstadoBadge(transaction.estado)}</TableCell>
                <TableCell>
                  {transaction.transaccion?.modalidad || '-'}
                </TableCell>
                <TableCell>
                  {transaction.fechaEstadoCambio ? 
                    new Date(transaction.fechaEstadoCambio).toLocaleString('es-EC') : '-'}
                </TableCell>
                <TableCell>{transaction.detalle || '-'}</TableCell>
              </TableRow>
            ))
          )}
        </TableBody>
      </Table>
    </div>
  );
} 