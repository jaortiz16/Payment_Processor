'use client';

import { Bell, Search } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { ModeToggle } from "@/components/mode-toggle";

const notifications = [
  {
    id: 1,
    title: "Nueva transacción",
    description: "Se procesó un pago por $1,200.00",
    time: "Hace 5 minutos"
  },
  {
    id: 2,
    title: "Alerta de fraude",
    description: "Actividad sospechosa detectada",
    time: "Hace 10 minutos"
  },
  {
    id: 3,
    title: "Banco desconectado",
    description: "Banco Regional perdió conexión",
    time: "Hace 15 minutos"
  }
];

export function Header() {
  return (
    <div className="border-b">
      <div className="flex h-16 items-center px-4">
        <div className="flex-1">
          <div className="relative max-w-md">
            <Search className="absolute left-2 top-2.5 h-4 w-4 icon-muted" />
            <Input placeholder="Buscar..." className="pl-8" />
          </div>
        </div>
        <div className="flex items-center gap-4">
          <ModeToggle />
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" size="icon" className="relative hover:bg-accent/50">
                <Bell className="h-5 w-5 icon-primary" />
                <span className="absolute -top-1 -right-1 h-4 w-4 rounded-full bg-blue-600 text-[10px] font-medium text-white flex items-center justify-center">
                  3
                </span>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" className="w-80">
              <DropdownMenuLabel>Notificaciones</DropdownMenuLabel>
              <DropdownMenuSeparator />
              {notifications.map((notification) => (
                <DropdownMenuItem key={notification.id} className="flex flex-col items-start gap-1 p-3">
                  <div className="font-medium">{notification.title}</div>
                  <div className="text-sm text-muted-foreground">{notification.description}</div>
                  <div className="text-xs text-muted-foreground">{notification.time}</div>
                </DropdownMenuItem>
              ))}
              <DropdownMenuSeparator />
              <DropdownMenuItem className="text-center justify-center text-sm text-blue-600 hover:text-blue-700">
                Ver todas las notificaciones
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" className="relative h-8 w-fit gap-2 px-2 hover:bg-accent/50">
                <Avatar className="h-8 w-8">
                  <AvatarImage src="/avatars/01.png" alt="@usuario" />
                  <AvatarFallback>MJ</AvatarFallback>
                </Avatar>
                <div className="flex flex-col items-start">
                  <span className="text-sm font-medium">Manik Jingga</span>
                  <span className="text-xs text-muted-foreground">Admin Store</span>
                </div>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent className="w-56" align="end">
              <DropdownMenuItem className="hover:bg-accent/50">
                Mi Perfil
              </DropdownMenuItem>
              <DropdownMenuItem className="hover:bg-accent/50">
                Configuración
              </DropdownMenuItem>
              <DropdownMenuSeparator />
              <DropdownMenuItem className="hover:bg-accent/50 text-red-600">
                Cerrar Sesión
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </div>
    </div>
  );
} 