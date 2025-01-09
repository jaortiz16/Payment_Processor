'use client';

import { useEffect, useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription, CardFooter } from "@/components/ui/card";
import { Shield, AlertTriangle, Ban, LineChart, TrendingUp, Search } from "lucide-react";
import { Area, AreaChart, Bar, BarChart, CartesianGrid, XAxis, ResponsiveContainer } from "recharts";
import { ChartConfig, ChartContainer, ChartTooltip, ChartTooltipContent } from "@/components/ui/chart";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { DatePickerWithRange } from "@/components/ui/date-range-picker";
import { addDays, format } from "date-fns";
import { es } from "date-fns/locale";
import { cn } from "@/lib/utils";
import { useFraudMonitoringStore } from "@/store/fraudMonitoringStore";
import { DateRange } from "react-day-picker";

// Mantener los datos de ejemplo para los gráficos
const fraudData = [
  { month: "January", intentos: 186, bloqueados: 80 },
  { month: "February", intentos: 305, bloqueados: 150 },
  { month: "March", intentos: 237, bloqueados: 120 },
  { month: "April", intentos: 273, bloqueados: 190 },
  { month: "May", intentos: 309, bloqueados: 230 },
  { month: "June", intentos: 214, bloqueados: 140 },
];

const alertsData = [
  { date: "2024-06-24", monto: 132, ip: 180 },
  { date: "2024-06-25", monto: 141, ip: 190 },
  { date: "2024-06-26", monto: 434, ip: 380 },
  { date: "2024-06-27", monto: 448, ip: 490 },
  { date: "2024-06-28", monto: 349, ip: 300 },
  { date: "2024-06-29", monto: 303, ip: 260 },
  { date: "2024-06-30", monto: 446, ip: 400 },
];

const chartConfig = {
  intentos: {
    label: "Intentos",
    color: "hsl(var(--chart-1))",
  },
  bloqueados: {
    label: "Bloqueados",
    color: "hsl(var(--chart-2))",
  },
  monto: {
    label: "Monto Inusual",
    color: "hsl(var(--chart-1))",
  },
  ip: {
    label: "IP Sospechosa",
    color: "hsl(var(--chart-2))",
  },
} satisfies ChartConfig;

const riesgoStyles = {
  ALT: "bg-red-600 text-white",
  MED: "bg-yellow-600 text-white",
  BAJ: "bg-green-600 text-white",
};

const estadoStyles = {
  PEN: "bg-yellow-600 text-white",
  PRO: "bg-green-600 text-white",
};

export default function FraudMonitoringPage() {
  const { alerts, isLoading, error, fetchPendingAlerts, fetchAlertsByDate, fetchAlertsByTransaction, processAlert } = useFraudMonitoringStore();
  const [activeChart, setActiveChart] = useState<"monto" | "ip">("monto");
  const [searchTerm, setSearchTerm] = useState('');
  const [mounted, setMounted] = useState(false);
  const [date, setDate] = useState<DateRange | undefined>({
    from: addDays(new Date(), -7),
    to: new Date(),
  });

  useEffect(() => {
    setMounted(true);
  }, []);

  useEffect(() => {
    if (!mounted) return;

    if (date?.from && date?.to) {
      fetchAlertsByDate(
        date.from.toISOString(),
        date.to.toISOString()
      );
    } else {
      fetchPendingAlerts();
    }
  }, [date, fetchAlertsByDate, fetchPendingAlerts, mounted]);

  if (!mounted) {
    return null;
  }

  const handleSearch = (value: string) => {
    setSearchTerm(value);
    if (value.trim()) {
      const transactionId = parseInt(value);
      if (!isNaN(transactionId)) {
        fetchAlertsByTransaction(transactionId);
      }
    } else if (date?.from && date?.to) {
      fetchAlertsByDate(
        date.from.toISOString(),
        date.to.toISOString()
      );
    } else {
      fetchPendingAlerts();
    }
  };

  const handleProcessAlert = async (id: number, estado: string) => {
    if (window.confirm('¿Está seguro de procesar esta alerta?')) {
      await processAlert(id, estado, 'Alerta procesada por el usuario');
    }
  };

  // Mantener el código existente de los gráficos y las tarjetas de resumen
  const total = {
    monto: alertsData.reduce((acc, curr) => acc + curr.monto, 0),
    ip: alertsData.reduce((acc, curr) => acc + curr.ip, 0),
  };

  return (
    <div className="space-y-4">
      <h2 className="text-3xl font-bold tracking-tight">Monitoreo de Fraude</h2>
      
      {/* Mantener las tarjetas de resumen existentes */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <Card className="relative overflow-hidden">
          <div className="absolute right-0 top-0 h-24 w-24 translate-x-8 translate-y-[-8px] transform">
            <div className="absolute inset-0 bg-gradient-to-br from-blue-500/20 to-transparent blur-2xl" />
          </div>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">
              Score de Riesgo
            </CardTitle>
            <div className="rounded-full bg-blue-500/20 p-2">
              <Shield className="h-4 w-4 text-blue-500" />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-blue-500">0.82</div>
            <p className="text-xs text-muted-foreground">
              Bajo riesgo
            </p>
          </CardContent>
        </Card>
        <Card className="relative overflow-hidden">
          <div className="absolute right-0 top-0 h-24 w-24 translate-x-8 translate-y-[-8px] transform">
            <div className="absolute inset-0 bg-gradient-to-br from-yellow-500/20 to-transparent blur-2xl" />
          </div>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">
              Alertas Activas
            </CardTitle>
            <div className="rounded-full bg-yellow-500/20 p-2">
              <AlertTriangle className="h-4 w-4 text-yellow-500" />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-yellow-500">23</div>
            <p className="text-xs text-muted-foreground">
              +5 en la última hora
            </p>
          </CardContent>
        </Card>
        <Card className="relative overflow-hidden">
          <div className="absolute right-0 top-0 h-24 w-24 translate-x-8 translate-y-[-8px] transform">
            <div className="absolute inset-0 bg-gradient-to-br from-red-500/20 to-transparent blur-2xl" />
          </div>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">
              Transacciones Bloqueadas
            </CardTitle>
            <div className="rounded-full bg-red-500/20 p-2">
              <Ban className="h-4 w-4 text-red-500" />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-red-500">12</div>
            <p className="text-xs text-muted-foreground">
              En las últimas 24 horas
            </p>
          </CardContent>
        </Card>
        <Card className="relative overflow-hidden">
          <div className="absolute right-0 top-0 h-24 w-24 translate-x-8 translate-y-[-8px] transform">
            <div className="absolute inset-0 bg-gradient-to-br from-green-500/20 to-transparent blur-2xl" />
          </div>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">
              Tasa de Detección
            </CardTitle>
            <div className="rounded-full bg-green-500/20 p-2">
              <LineChart className="h-4 w-4 text-green-500" />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-green-500">95.3%</div>
            <p className="text-xs text-muted-foreground">
              +2.1% respecto a ayer
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Mantener los gráficos existentes */}
      <div className="grid gap-4 md:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>Intentos de Fraude vs Bloqueados</CardTitle>
            <CardDescription>
              Mostrando datos de los últimos 6 meses
            </CardDescription>
          </CardHeader>
          <CardContent>
            <ChartContainer config={chartConfig}>
              <div className="h-[300px] w-full">
                <ResponsiveContainer width="100%" height="100%">
                  <AreaChart
                    data={fraudData}
                    margin={{
                      left: 12,
                      right: 12,
                    }}
                  >
                    <CartesianGrid vertical={false} />
                    <XAxis
                      dataKey="month"
                      tickLine={false}
                      axisLine={false}
                      tickMargin={8}
                      tickFormatter={(value) => value.slice(0, 3)}
                    />
                    <ChartTooltip
                      cursor={false}
                      content={<ChartTooltipContent indicator="dot" />}
                    />
                    <Area
                      dataKey="intentos"
                      type="monotone"
                      fill="hsl(var(--chart-1))"
                      fillOpacity={0.2}
                      stroke="hsl(var(--chart-1))"
                      strokeWidth={2}
                    />
                    <Area
                      dataKey="bloqueados"
                      type="monotone"
                      fill="hsl(var(--chart-2))"
                      fillOpacity={0.2}
                      stroke="hsl(var(--chart-2))"
                      strokeWidth={2}
                    />
                  </AreaChart>
                </ResponsiveContainer>
              </div>
            </ChartContainer>
          </CardContent>
          <CardFooter>
            <div className="flex w-full items-start gap-2 text-sm">
              <div className="grid gap-2">
                <div className="flex items-center gap-2 font-medium leading-none">
                  Incremento del 5.2% este mes <TrendingUp className="h-4 w-4" />
                </div>
                <div className="flex items-center gap-2 leading-none text-muted-foreground">
                  Enero - Junio 2024
                </div>
              </div>
            </div>
          </CardFooter>
        </Card>

        <Card>
          <CardHeader className="flex flex-col items-stretch space-y-0 border-b p-0 sm:flex-row">
            <div className="flex flex-1 flex-col justify-center gap-1 px-6 py-5 sm:py-6">
              <CardTitle>Tipos de Alerta</CardTitle>
              <CardDescription>
                Últimos 7 días
              </CardDescription>
            </div>
            <div className="flex">
              {["monto", "ip"].map((key) => {
                const chart = key as keyof typeof total;
                return (
                  <button
                    key={chart}
                    data-active={activeChart === chart}
                    className="relative z-30 flex flex-1 flex-col justify-center gap-1 border-t px-6 py-4 text-left even:border-l data-[active=true]:bg-muted/50 sm:border-l sm:border-t-0 sm:px-8 sm:py-6"
                    onClick={() => setActiveChart(chart as "monto" | "ip")}
                  >
                    <span className="text-xs text-muted-foreground">
                      {chartConfig[chart].label}
                    </span>
                    <span className="text-lg font-bold leading-none sm:text-3xl">
                      {total[chart].toLocaleString()}
                    </span>
                  </button>
                );
              })}
            </div>
          </CardHeader>
          <CardContent className="px-2 sm:p-6">
            <ChartContainer config={chartConfig}>
              <div className="h-[300px] w-full">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart
                    data={alertsData}
                    margin={{
                      left: 12,
                      right: 12,
                    }}
                  >
                    <CartesianGrid vertical={false} />
                    <XAxis
                      dataKey="date"
                      tickLine={false}
                      axisLine={false}
                      tickMargin={8}
                      minTickGap={32}
                      tickFormatter={(value) => {
                        const date = new Date(value);
                        return date.toLocaleDateString("es-ES", {
                          month: "short",
                          day: "numeric",
                        });
                      }}
                    />
                    <ChartTooltip
                      cursor={false}
                      content={<ChartTooltipContent indicator="dot" />}
                    />
                    <Bar 
                      dataKey={activeChart} 
                      fill={`hsl(var(--chart-${activeChart === "monto" ? "1" : "2"}))`}
                      radius={[4, 4, 0, 0]}
                    />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </ChartContainer>
          </CardContent>
        </Card>
      </div>

      {/* Nueva sección de filtros y tabla */}
      <Card>
        <CardHeader>
          <CardTitle>Alertas de Fraude</CardTitle>
          <CardDescription>
            Monitoreo y gestión de alertas de fraude
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex items-center gap-4 mb-4">
            <div className="flex-1">
              <DatePickerWithRange date={date} onDateChange={setDate} />
            </div>
            <div className="relative flex-1">
              <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="Buscar por código de transacción..."
                className="pl-8"
                value={searchTerm}
                onChange={(e) => handleSearch(e.target.value)}
              />
            </div>
          </div>

          <div className="rounded-md border">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Código</TableHead>
                  <TableHead>Regla</TableHead>
                  <TableHead>Transacción</TableHead>
                  <TableHead>Nivel Riesgo</TableHead>
                  <TableHead>Puntaje</TableHead>
                  <TableHead>Estado</TableHead>
                  <TableHead>Fecha Detección</TableHead>
                  <TableHead>Acciones</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {alerts.map((alert) => (
                  <TableRow key={alert.codigo}>
                    <TableCell className="font-medium">
                      {alert.codigoUnicoTransaccion}
                    </TableCell>
                    <TableCell>
                      {alert.reglaFraude.nombre}
                      <br />
                      <span className="text-xs text-muted-foreground">
                        {alert.reglaFraude.tipoRegla}
                      </span>
                    </TableCell>
                    <TableCell>
                      ${alert.transaccion.monto.toFixed(2)}
                      <br />
                      <span className="text-xs text-muted-foreground">
                        {alert.transaccion.numeroTarjeta.replace(/(\d{4})/g, '$1 ').trim()}
                      </span>
                    </TableCell>
                    <TableCell>
                      <span className={cn(
                        "px-2 py-1 rounded-full text-xs font-medium",
                        riesgoStyles[alert.nivelRiesgo as keyof typeof riesgoStyles]
                      )}>
                        {alert.nivelRiesgo === 'ALT' ? 'Alto' : 
                         alert.nivelRiesgo === 'MED' ? 'Medio' : 'Bajo'}
                      </span>
                    </TableCell>
                    <TableCell>{alert.puntajeRiesgo.toFixed(2)}</TableCell>
                    <TableCell>
                      <span className={cn(
                        "px-2 py-1 rounded-full text-xs font-medium",
                        estadoStyles[alert.estado as keyof typeof estadoStyles]
                      )}>
                        {alert.estado === 'PEN' ? 'Pendiente' : 'Procesado'}
                      </span>
                    </TableCell>
                    <TableCell>
                      {format(new Date(alert.fechaDeteccion), 'dd/MM/yyyy HH:mm', { locale: es })}
                    </TableCell>
                    <TableCell>
                      {alert.estado === 'PEN' && (
                        <div className="flex gap-2">
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => handleProcessAlert(alert.codigo, 'PRO')}
                          >
                            Procesar
                          </Button>
                        </div>
                      )}
                    </TableCell>
                  </TableRow>
                ))}
                {alerts.length === 0 && (
                  <TableRow>
                    <TableCell colSpan={8} className="text-center py-4">
                      No se encontraron alertas
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </div>
        </CardContent>
      </Card>
    </div>
  );
} 