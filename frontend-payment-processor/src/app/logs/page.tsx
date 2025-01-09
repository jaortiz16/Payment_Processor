'use client';

import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Search, Download, SlidersHorizontal, CalendarRange } from "lucide-react";
import { useState } from "react";
import { cn } from "@/lib/utils";

type LogStatus = 'success' | 'error' | 'warning';

interface Log {
  id: number;
  timestamp: string;
  bank: string;
  event: string;
  status: LogStatus;
  details: string;
}

const logs: Log[] = [
  {
    id: 1,
    timestamp: "2024-01-06 10:30:25",
    bank: "Banco Nacional",
    event: "Conexión establecida",
    status: "success",
    details: "Conexión API exitosa",
  },
  {
    id: 2,
    timestamp: "2024-01-06 10:29:15",
    bank: "Banco del Estado",
    event: "Error de timeout",
    status: "error",
    details: "La conexión excedió el tiempo de espera",
  },
  {
    id: 3,
    timestamp: "2024-01-06 10:28:00",
    bank: "Banco Regional",
    event: "Sincronización completada",
    status: "success",
    details: "Se sincronizaron 150 transacciones",
  },
  {
    id: 4,
    timestamp: "2024-01-06 10:27:30",
    bank: "Banco Cooperativo",
    event: "Error de autenticación",
    status: "error",
    details: "Credenciales inválidas",
  },
];

const statusStyles: Record<LogStatus, string> = {
  success: "bg-green-600 text-white",
  error: "bg-red-600 text-white",
  warning: "bg-yellow-600 text-white",
};

const statusText: Record<LogStatus, string> = {
  success: "Exitoso",
  error: "Error",
  warning: "Advertencia",
};

export default function LogsPage() {
  const [status, setStatus] = useState<string>("all");
  const [bank, setBank] = useState<string>("all");

  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h2 className="text-3xl font-bold tracking-tight">Logs de Conexión</h2>
        <div className="flex gap-2">
          <Button variant="outline">
            <CalendarRange className="mr-2 h-4 w-4" /> Rango de Fechas
          </Button>
          <Button variant="outline">
            <Download className="mr-2 h-4 w-4" /> Exportar Logs
          </Button>
        </div>
      </div>

      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div className="flex flex-1 gap-2 items-center max-w-sm">
          <div className="relative flex-1">
            <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input placeholder="Buscar en logs..." className="pl-8" />
          </div>
          <Button variant="outline" size="icon">
            <SlidersHorizontal className="h-4 w-4" />
          </Button>
        </div>

        <div className="flex gap-2">
          <Select value={bank} onValueChange={setBank}>
            <SelectTrigger className="w-[180px]">
              <SelectValue placeholder="Filtrar por banco" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">Todos los bancos</SelectItem>
              <SelectItem value="nacional">Banco Nacional</SelectItem>
              <SelectItem value="estado">Banco del Estado</SelectItem>
              <SelectItem value="regional">Banco Regional</SelectItem>
              <SelectItem value="cooperativo">Banco Cooperativo</SelectItem>
            </SelectContent>
          </Select>

          <Select value={status} onValueChange={setStatus}>
            <SelectTrigger className="w-[180px]">
              <SelectValue placeholder="Filtrar por estado" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">Todos los estados</SelectItem>
              <SelectItem value="success">Exitoso</SelectItem>
              <SelectItem value="error">Error</SelectItem>
              <SelectItem value="warning">Advertencia</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </div>

      <div className="rounded-md border bg-card">
        <Table>
          <TableHeader>
            <TableRow className="hover:bg-transparent">
              <TableHead className="w-[180px]">Fecha y Hora</TableHead>
              <TableHead>Banco</TableHead>
              <TableHead>Evento</TableHead>
              <TableHead>Estado</TableHead>
              <TableHead className="max-w-[500px]">Detalles</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {logs.map((log) => (
              <TableRow key={log.id} className="group">
                <TableCell className="font-mono text-sm">
                  {log.timestamp}
                </TableCell>
                <TableCell className="font-medium">{log.bank}</TableCell>
                <TableCell>{log.event}</TableCell>
                <TableCell>
                  <span
                    className={cn(
                      "inline-flex items-center rounded-md px-2.5 py-0.5 text-xs font-medium",
                      statusStyles[log.status]
                    )}
                  >
                    {statusText[log.status]}
                  </span>
                </TableCell>
                <TableCell className="text-muted-foreground">
                  {log.details}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
    </div>
  );
} 