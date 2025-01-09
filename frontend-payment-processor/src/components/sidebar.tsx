'use client';

import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import { ChevronLeft, LayoutDashboard, CreditCard, Shield, Building2, FileText, Menu, Settings, HelpCircle, Store, ChevronDown, AlertCircle, LineChart } from "lucide-react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { useEffect, useState } from "react";

interface SidebarProps extends React.HTMLAttributes<HTMLDivElement> {}

export function Sidebar({ className }: SidebarProps) {
  const pathname = usePathname();
  const [isCollapsed, setIsCollapsed] = useState(false);
  const [isFraudeOpen, setIsFraudeOpen] = useState(false);

  useEffect(() => {
    const savedState = localStorage.getItem('sidebarCollapsed');
    if (savedState !== null) {
      setIsCollapsed(JSON.parse(savedState));
    }
  }, []);

  const toggleCollapsed = () => {
    const newState = !isCollapsed;
    setIsCollapsed(newState);
    localStorage.setItem('sidebarCollapsed', JSON.stringify(newState));
  };

  const toggleFraude = (e: React.MouseEvent) => {
    e.preventDefault();
    setIsFraudeOpen(!isFraudeOpen);
  };

  return (
    <div 
      className={cn(
        "fixed top-0 left-0 z-40 flex flex-col h-screen border-r bg-background",
        isCollapsed ? "w-[60px]" : "w-64",
        className
      )}
    >
      <div className="flex items-center justify-between px-4 h-16">
        <div className={cn(
          "flex items-center gap-2 overflow-hidden transition-all duration-300",
          isCollapsed ? "w-0" : "w-full"
        )}>
          <Store className="h-6 w-6 icon-primary flex-shrink-0" />
          <h1 className="text-xl font-bold whitespace-nowrap">PaymentProcessor</h1>
        </div>
        <Button 
          variant="ghost" 
          size="icon"
          onClick={toggleCollapsed}
          className="hover:bg-accent/50 shrink-0"
        >
          {isCollapsed ? <Menu className="h-4 w-4 icon-primary" /> : <ChevronLeft className="h-4 w-4 icon-primary" />}
        </Button>
      </div>

      <div className="flex-1 overflow-y-auto">
        <div className="space-y-2 px-2">
          <Link
            href="/dashboard"
            className={cn(
              "flex items-center gap-x-2 rounded-lg px-3 py-2 text-sm font-medium transition-colors",
              pathname === "/dashboard" || pathname === "/" 
                ? "bg-blue-600 text-white hover:bg-blue-700" 
                : "text-muted-foreground hover:bg-blue-600 hover:text-white",
              isCollapsed && "justify-center px-2"
            )}
          >
            <LayoutDashboard className="h-5 w-5 shrink-0" />
            <span className={cn(
              "overflow-hidden transition-all duration-300",
              isCollapsed ? "w-0" : "w-auto"
            )}>
              Dashboard
            </span>
          </Link>

          {[
            { label: "Transacciones", icon: CreditCard, href: "/transacciones" },
          ].map((route) => (
            <Link
              key={route.href}
              href={route.href}
              className={cn(
                "flex items-center gap-x-2 rounded-lg px-3 py-2 text-sm font-medium transition-colors",
                pathname === route.href 
                  ? "bg-accent text-accent-foreground" 
                  : "text-muted-foreground hover:bg-accent/50 hover:text-foreground",
                isCollapsed && "justify-center px-2"
              )}
            >
              <route.icon className="h-5 w-5 shrink-0" />
              <span className={cn(
                "overflow-hidden transition-all duration-300",
                isCollapsed ? "w-0" : "w-auto"
              )}>
                {route.label}
              </span>
            </Link>
          ))}

          <div>
            <button
              onClick={toggleFraude}
              className={cn(
                "w-full flex items-center justify-between gap-x-2 rounded-lg px-3 py-2 text-sm font-medium transition-colors text-muted-foreground hover:bg-accent/50 hover:text-foreground",
                isCollapsed && "justify-center px-2"
              )}
            >
              <div className="flex items-center gap-x-2">
                <Shield className="h-5 w-5 shrink-0" />
                <span className={cn(
                  "overflow-hidden transition-all duration-300",
                  isCollapsed ? "w-0" : "w-auto"
                )}>
                  Fraude
                </span>
              </div>
              {!isCollapsed && (
                <ChevronDown className={cn(
                  "h-4 w-4 shrink-0 transition-transform duration-200",
                  isFraudeOpen && "rotate-180"
                )} />
              )}
            </button>
            {isFraudeOpen && !isCollapsed && (
              <div className="ml-4 mt-2 space-y-1">
                <Link
                  href="/fraude/reglas"
                  className={cn(
                    "flex items-center gap-x-2 rounded-lg px-3 py-2 text-sm font-medium transition-colors",
                    pathname === "/fraude/reglas" 
                      ? "bg-accent text-accent-foreground" 
                      : "text-muted-foreground hover:bg-accent/50 hover:text-foreground"
                  )}
                >
                  <AlertCircle className="h-5 w-5 shrink-0" />
                  <span>Reglas</span>
                </Link>
                <Link
                  href="/fraude/monitoreo"
                  className={cn(
                    "flex items-center gap-x-2 rounded-lg px-3 py-2 text-sm font-medium transition-colors",
                    pathname === "/fraude/monitoreo" 
                      ? "bg-accent text-accent-foreground" 
                      : "text-muted-foreground hover:bg-accent/50 hover:text-foreground"
                  )}
                >
                  <LineChart className="h-5 w-5 shrink-0" />
                  <span>Monitoreo</span>
                </Link>
              </div>
            )}
          </div>

          {[
            { label: "Bancos", icon: Building2, href: "/bancos" },
            { label: "Comisiones", icon: CreditCard, href: "/comisiones" },
            { label: "Logs", icon: FileText, href: "/logs" },
            { label: "Reportes", icon: FileText, href: "/reportes" },
          ].map((route) => (
            <Link
              key={route.href}
              href={route.href}
              className={cn(
                "flex items-center gap-x-2 rounded-lg px-3 py-2 text-sm font-medium transition-colors",
                pathname === route.href 
                  ? "bg-accent text-accent-foreground" 
                  : "text-muted-foreground hover:bg-accent/50 hover:text-foreground",
                isCollapsed && "justify-center px-2"
              )}
            >
              <route.icon className="h-5 w-5 shrink-0" />
              <span className={cn(
                "overflow-hidden transition-all duration-300",
                isCollapsed ? "w-0" : "w-auto"
              )}>
                {route.label}
              </span>
            </Link>
          ))}
        </div>
      </div>

      <div className="border-t p-2">
        {[
          { label: "Settings", icon: Settings, href: "/settings" },
          { label: "Help Center", icon: HelpCircle, href: "/help" }
        ].map((route) => (
          <Link
            key={route.href}
            href={route.href}
            className={cn(
              "flex items-center gap-x-2 rounded-lg px-3 py-2 text-sm font-medium transition-colors",
              pathname === route.href 
                ? "bg-accent text-accent-foreground" 
                : "text-muted-foreground hover:bg-accent/50 hover:text-foreground",
              isCollapsed && "justify-center px-2"
            )}
          >
            <route.icon className="h-5 w-5 shrink-0" />
            <span className={cn(
              "overflow-hidden transition-all duration-300",
              isCollapsed ? "w-0" : "w-auto"
            )}>
              {route.label}
            </span>
          </Link>
        ))}
      </div>
    </div>
  );
} 