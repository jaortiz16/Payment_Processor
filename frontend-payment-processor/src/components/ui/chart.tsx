"use client"

import * as React from "react"
import { TooltipProps } from "recharts"
import { Legend, Tooltip } from "recharts"
import { cn } from "@/lib/utils"

export type ChartConfig = Record<
  string,
  {
    label: string
    color?: string
  }
>

export interface ChartTooltipContentProps extends TooltipProps<any, any> {
  hideLabel?: boolean
  indicator?: "dashed" | "dot"
}

export function ChartTooltipContent({
  active,
  payload,
  label,
  hideLabel,
  indicator = "dashed",
}: ChartTooltipContentProps) {
  if (!active || !payload) {
    return null
  }

  return (
    <div className="rounded-lg border bg-background p-2 shadow-sm">
      {!hideLabel && (
        <div className="grid gap-2">
          <p className="text-xs font-medium text-muted-foreground">{label}</p>
          <div className="h-px bg-muted" />
        </div>
      )}
      <div className="grid gap-2">
        {payload.map(({ name, value, color }) => (
          <div key={name} className="flex items-center justify-between gap-2">
            <div className="flex items-center gap-1">
              {indicator === "dashed" ? (
                <div
                  className="h-px w-3"
                  style={{
                    backgroundColor: color,
                  }}
                />
              ) : (
                <div
                  className="h-1.5 w-1.5 rounded-full"
                  style={{
                    backgroundColor: color,
                  }}
                />
              )}
              <p className="text-xs font-medium text-muted-foreground">{name}</p>
            </div>
            <p className="text-right text-xs font-medium">
              {typeof value === "number" ? value.toLocaleString() : value}
            </p>
          </div>
        ))}
      </div>
    </div>
  )
}

export interface ChartLegendContentProps {
  payload?: {
    value: string
    color: string
  }[]
}

export function ChartLegendContent({ payload }: ChartLegendContentProps) {
  if (!payload) {
    return null
  }

  return (
    <div className="flex items-center gap-4">
      {payload.map(({ value, color }) => (
        <div key={value} className="flex items-center gap-1">
          <div
            className="h-1.5 w-1.5 rounded-full"
            style={{
              backgroundColor: color,
            }}
          />
          <p className="text-xs font-medium text-muted-foreground">{value}</p>
        </div>
      ))}
    </div>
  )
}

export interface ChartContainerProps
  extends React.HTMLAttributes<HTMLDivElement> {
  config: ChartConfig
}

export function ChartContainer({
  config,
  children,
  className,
  ...props
}: ChartContainerProps) {
  const style = React.useMemo(() => {
    return Object.entries(config).reduce((acc, [key, value]) => {
      if (!value.color) {
        return acc
      }

      return {
        ...acc,
        [`--color-${key}`]: value.color,
      }
    }, {})
  }, [config])

  return (
    <div
      className={cn("relative", className)}
      style={style}
      {...props}
    >
      {children}
    </div>
  )
}

export function ChartTooltip(props: TooltipProps<any, any>) {
  return <Tooltip {...props} wrapperStyle={{ outline: "none" }} />
}

export function ChartLegend(props: any) {
  return <Legend {...props} />
} 