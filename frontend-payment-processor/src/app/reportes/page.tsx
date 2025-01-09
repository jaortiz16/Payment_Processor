import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { FileText, Download, BarChart, PieChart, TrendingUp, Calendar, Plus } from "lucide-react";

const reports = [
  {
    id: 1,
    name: "Reporte de Transacciones",
    description: "Resumen detallado de todas las transacciones procesadas",
    icon: BarChart,
    lastGenerated: "Hace 2 horas",
    type: "daily",
  },
  {
    id: 2,
    name: "Análisis de Fraude",
    description: "Patrones y detección de actividades sospechosas",
    icon: PieChart,
    lastGenerated: "Hace 1 día",
    type: "weekly",
  },
  {
    id: 3,
    name: "Rendimiento por Banco",
    description: "Métricas de rendimiento y comparativas entre bancos",
    icon: TrendingUp,
    lastGenerated: "Hace 5 días",
    type: "monthly",
  },
  {
    id: 4,
    name: "Reporte de Comisiones",
    description: "Desglose de comisiones y ganancias por banco",
    icon: FileText,
    lastGenerated: "Hace 1 mes",
    type: "monthly",
  },
];

export default function ReportsPage() {
  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h2 className="text-3xl font-bold tracking-tight">Reportes</h2>
        <Button variant="outline">
          <Calendar className="mr-2 h-4 w-4" />
          Programar Reporte
        </Button>
      </div>
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-2">
        {reports.map((report) => (
          <Card key={report.id}>
            <CardHeader>
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-2">
                  <div className="p-2 bg-primary/10 rounded-full">
                    <report.icon className="h-4 w-4 text-primary" />
                  </div>
                  <div>
                    <CardTitle>{report.name}</CardTitle>
                    <CardDescription className="mt-1">
                      {report.description}
                    </CardDescription>
                  </div>
                </div>
              </div>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <div className="flex items-center justify-between text-sm">
                  <span className="text-muted-foreground">Última generación:</span>
                  <span>{report.lastGenerated}</span>
                </div>
                <div className="flex items-center justify-between text-sm">
                  <span className="text-muted-foreground">Frecuencia:</span>
                  <span className="capitalize">{report.type}</span>
                </div>
                <div className="flex items-center justify-between">
                  <Button variant="outline" size="sm">
                    Generar Ahora
                  </Button>
                  <Button variant="ghost" size="sm">
                    <Download className="h-4 w-4" />
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
      <Card>
        <CardHeader>
          <CardTitle>Reportes Personalizados</CardTitle>
          <CardDescription>
            Crea reportes personalizados seleccionando métricas específicas y rangos de fecha
          </CardDescription>
        </CardHeader>
        <CardContent>
          <Button className="w-full">
            <Plus className="mr-2 h-4 w-4" /> Crear Reporte Personalizado
          </Button>
        </CardContent>
      </Card>
    </div>
  );
} 