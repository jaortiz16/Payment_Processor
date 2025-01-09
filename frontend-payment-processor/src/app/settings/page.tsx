import { Settings, Bell, Lock, User, Palette } from "lucide-react";

export default function SettingsPage() {
  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold tracking-tight">Ajustes</h2>
        <p className="text-muted-foreground">
          Administra la configuración de tu cuenta y preferencias del sistema.
        </p>
      </div>

      <div className="grid gap-6">
        <div className="grid gap-4">
          {[
            {
              title: "Perfil",
              description: "Actualiza tu información personal y preferencias de cuenta",
              icon: User,
              href: "/settings/profile"
            },
            {
              title: "Notificaciones",
              description: "Configura cómo y cuándo recibes notificaciones",
              icon: Bell,
              href: "/settings/notifications"
            },
            {
              title: "Seguridad",
              description: "Gestiona la seguridad de tu cuenta y autenticación",
              icon: Lock,
              href: "/settings/security"
            },
            {
              title: "Apariencia",
              description: "Personaliza la apariencia y el tema de la aplicación",
              icon: Palette,
              href: "/settings/appearance"
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
                <h3 className="font-medium">{item.title}</h3>
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