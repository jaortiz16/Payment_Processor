'use client';

import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Calendar } from "@/components/ui/calendar";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { CalendarIcon, Download, Search, SlidersHorizontal, Loader2, X } from "lucide-react";
import { useState, useEffect, useCallback } from "react";
import { format, subDays } from "date-fns";
import { es } from "date-fns/locale";
import { cn } from "@/lib/utils";
import { useTransactionStore } from "@/store/transactionStore";
import { Card, CardContent } from "@/components/ui/card";
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";

const estadoStyles: Record<string, string> = {
  APR: "bg-green-600 text-white",
  PEN: "bg-yellow-600 text-white",
  REC: "bg-red-600 text-white",
  REV: "bg-purple-600 text-white",
};

const estadoText: Record<string, string> = {
  APR: "Aprobada",
  PEN: "Pendiente",
  REC: "Rechazada",
  REV: "En Revisión",
};

export default function TransactionsPage() {
  const [date, setDate] = useState<Date | undefined>(new Date());
  const [status, setStatus] = useState<string>("all");
  const [searchTerm, setSearchTerm] = useState("");
  const [isFiltersOpen, setIsFiltersOpen] = useState(false);

  const { transactions, isLoading, error, fetchTransactions } = useTransactionStore();

  const loadTransactions = useCallback((params: {
    estado?: string;
    fechaInicio?: string;
    fechaFin?: string;
    bancoNombre?: string;
  }) => {
    fetchTransactions(params);
  }, [fetchTransactions]);

  useEffect(() => {
    // Por defecto, cargar transacciones de los últimos 7 días
    const endDate = new Date();
    const startDate = subDays(endDate, 7);
    
    loadTransactions({
      estado: status === "all" ? undefined : status,
      fechaInicio: startDate.toISOString(),
      fechaFin: endDate.toISOString(),
      bancoNombre: searchTerm || undefined
    });
  }, [status, searchTerm, loadTransactions]);

  const handleDateSelect = (selectedDate: Date | undefined) => {
    setDate(selectedDate);
    if (selectedDate) {
      const formattedDate = selectedDate.toISOString();
      loadTransactions({
        estado: status === "all" ? undefined : status,
        fechaInicio: formattedDate,
        fechaFin: formattedDate,
        bancoNombre: searchTerm || undefined
      });
    }
  };

  const handleSearch = (value: string) => {
    setSearchTerm(value);
  };

  const clearFilters = () => {
    setStatus("all");
    setDate(undefined);
    setSearchTerm("");
    const endDate = new Date();
    const startDate = subDays(endDate, 7);
    loadTransactions({
      fechaInicio: startDate.toISOString(),
      fechaFin: endDate.toISOString()
    });
  };

  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h2 className="text-3xl font-bold tracking-tight">Transacciones</h2>
        <Button variant="outline">
          <Download className="mr-2 h-4 w-4" /> Exportar
        </Button>
      </div>

      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div className="flex flex-1 gap-2 items-center max-w-sm">
          <div className="relative flex-1">
            <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input 
              placeholder="Buscar por banco..." 
              className="pl-8" 
              value={searchTerm}
              onChange={(e) => handleSearch(e.target.value)}
            />
          </div>
          <Sheet open={isFiltersOpen} onOpenChange={setIsFiltersOpen}>
            <SheetTrigger asChild>
              <Button variant="outline" size="icon">
                <SlidersHorizontal className="h-4 w-4" />
              </Button>
            </SheetTrigger>
            <SheetContent>
              <SheetHeader>
                <SheetTitle>Filtros</SheetTitle>
                <SheetDescription>
                  Aplica filtros para refinar tu búsqueda
                </SheetDescription>
              </SheetHeader>
              <div className="grid gap-4 py-4">
                <div className="space-y-2">
                  <h4 className="font-medium">Estado</h4>
                  <Select value={status} onValueChange={setStatus}>
                    <SelectTrigger>
                      <SelectValue placeholder="Filtrar por estado" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="all">Todos los estados</SelectItem>
                      <SelectItem value="APR">Aprobada</SelectItem>
                      <SelectItem value="PEN">Pendiente</SelectItem>
                      <SelectItem value="REC">Rechazada</SelectItem>
                      <SelectItem value="REV">En Revisión</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                <div className="space-y-2">
                  <h4 className="font-medium">Fecha</h4>
                  <Calendar
                    mode="single"
                    selected={date}
                    onSelect={handleDateSelect}
                    className="rounded-md border shadow"
                    locale={es}
                  />
                </div>
                <Button onClick={clearFilters} variant="outline" className="mt-4">
                  <X className="mr-2 h-4 w-4" /> Limpiar Filtros
                </Button>
              </div>
            </SheetContent>
          </Sheet>
        </div>

        <div className="flex gap-2">
          <Select value={status} onValueChange={setStatus}>
            <SelectTrigger className="w-[180px]">
              <SelectValue placeholder="Filtrar por estado" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">Todos los estados</SelectItem>
              <SelectItem value="APR">Aprobada</SelectItem>
              <SelectItem value="PEN">Pendiente</SelectItem>
              <SelectItem value="REC">Rechazada</SelectItem>
              <SelectItem value="REV">En Revisión</SelectItem>
            </SelectContent>
          </Select>

          <Popover>
            <PopoverTrigger asChild>
              <Button
                variant="outline"
                className={cn(
                  "w-[190px] justify-start text-left font-normal",
                  !date && "text-muted-foreground"
                )}
              >
                <CalendarIcon className="mr-2 h-4 w-4" />
                {date ? format(date, "PPP", { locale: es }) : "Seleccionar fecha"}
              </Button>
            </PopoverTrigger>
            <PopoverContent className="w-auto p-0" align="end">
              <Calendar
                mode="single"
                selected={date}
                onSelect={handleDateSelect}
                initialFocus
                className="rounded-md border shadow"
                locale={es}
              />
            </PopoverContent>
          </Popover>
        </div>
      </div>

      {error && (
        <Card>
          <CardContent className="pt-6">
            <p className="text-red-500">{error}</p>
          </CardContent>
        </Card>
      )}

      <div className="rounded-md border">
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
                <TableCell colSpan={7} className="text-center py-10">
                  <div className="flex justify-center items-center">
                    <Loader2 className="h-6 w-6 animate-spin mr-2" />
                    Cargando transacciones...
                  </div>
                </TableCell>
              </TableRow>
            ) : transactions.length === 0 ? (
              <TableRow>
                <TableCell colSpan={7} className="text-center py-10">
                  No se encontraron transacciones
                </TableCell>
              </TableRow>
            ) : (
              transactions.map((transaction) => (
                <TableRow key={transaction.code} className="group hover:bg-muted/50">
                  <TableCell className="font-medium">{transaction.transaccion.codigo}</TableCell>
                  <TableCell>{transaction.transaccion.banco.nombreComercial}</TableCell>
                  <TableCell>
                    {new Intl.NumberFormat('es-EC', {
                      style: 'currency',
                      currency: transaction.transaccion.codigoMoneda
                    }).format(transaction.transaccion.monto)}
                  </TableCell>
                  <TableCell>
                    <span
                      className={cn(
                        "inline-flex items-center rounded-md px-2.5 py-0.5 text-xs font-medium",
                        estadoStyles[transaction.estado]
                      )}
                    >
                      {estadoText[transaction.estado]}
                    </span>
                  </TableCell>
                  <TableCell>
                    {transaction.transaccion.modalidad === 'SIM' ? 'Simple' : 'Recurrente'}
                  </TableCell>
                  <TableCell>
                    {format(new Date(transaction.fechaEstadoCambio), 'dd/MM/yyyy HH:mm')}
                  </TableCell>
                  <TableCell className="max-w-[200px] truncate">
                    {transaction.detalle || '-'}
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </div>
    </div>
  );
} 