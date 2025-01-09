import { HelpCircle, FileText, MessageCircle, Phone, Mail } from "lucide-react";

export default function HelpPage() {
  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold tracking-tight">Centro de Ayuda</h2>
        <p className="text-muted-foreground">
          Encuentra respuestas a tus preguntas y obtén soporte cuando lo necesites.
        </p>
      </div>

      <div className="grid gap-6 md:grid-cols-2">
        <div className="grid gap-4">
          <h3 className="text-lg font-semibold">Preguntas Frecuentes</h3>
          {[
            {
              title: "¿Cómo procesar un pago?",
              description: "Guía paso a paso para procesar pagos de manera segura",
              icon: FileText,
            },
            {
              title: "Configuración de Fraude",
              description: "Aprende a configurar las reglas de detección de fraude",
              icon: HelpCircle,
            },
            {
              title: "Integración de Bancos",
              description: "Proceso de integración con diferentes bancos",
              icon: FileText,
            }
          ].map((item) => (
            <div
              key={item.title}
              className="flex items-center gap-4 rounded-lg border p-4 hover:bg-accent/50 transition-colors cursor-pointer"
            >
              <div className="p-2 bg-background border rounded-full">
                <item.icon className="h-5 w-5 text-blue-600" />
              </div>
              <div>
                <h4 className="font-medium">{item.title}</h4>
                <p className="text-sm text-muted-foreground">
                  {item.description}
                </p>
              </div>
            </div>
          ))}
        </div>

        <div className="grid gap-4">
          <h3 className="text-lg font-semibold">Contacto</h3>
          {[
            {
              title: "Chat en Vivo",
              description: "Habla con un agente de soporte en tiempo real",
              icon: MessageCircle,
            },
            {
              title: "Soporte Telefónico",
              description: "Llámanos al +1 (555) 123-4567",
              icon: Phone,
            },
            {
              title: "Email",
              description: "soporte@paymentprocessor.com",
              icon: Mail,
            }
          ].map((item) => (
            <div
              key={item.title}
              className="flex items-center gap-4 rounded-lg border p-4 hover:bg-accent/50 transition-colors cursor-pointer"
            >
              <div className="p-2 bg-background border rounded-full">
                <item.icon className="h-5 w-5 text-blue-600" />
              </div>
              <div>
                <h4 className="font-medium">{item.title}</h4>
                <p className="text-sm text-muted-foreground">
                  {item.description}
                </p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
} 