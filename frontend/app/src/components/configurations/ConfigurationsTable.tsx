import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '../ui/table';
import { Badge } from '../ui/badge';
import { Button } from '../ui/button';
import { Pencil, Trash2 } from 'lucide-react';
import type { Configuration } from '../../types/configuration';
import { formatDate } from '../../utils/formatting';
import { getCategoryBadgeStyle } from '../../utils/formatting';

interface ConfigurationsTableProps {
  configurations: Configuration[];
  onEdit: (config: Configuration) => void;
  onDelete: (config: Configuration) => void;
}

export function ConfigurationsTable({
  configurations,
  onEdit,
  onDelete,
}: ConfigurationsTableProps) {
  if (configurations.length === 0) {
    return (
      <div className="text-center py-12">
        <p className="text-gray-500 dark:text-gray-400">No configurations found</p>
      </div>
    );
  }

  return (
    <div className="rounded-md border">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Category</TableHead>
            <TableHead>Key</TableHead>
            <TableHead>Value</TableHead>
            <TableHead>Description</TableHead>
            <TableHead>Last Updated</TableHead>
            <TableHead className="text-right">Actions</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {configurations.map((config) => {
            const badgeStyle = getCategoryBadgeStyle(config.category);
            return (
              <TableRow key={config.id}>
                <TableCell>
                  <Badge className={`${badgeStyle.bgColor} ${badgeStyle.textColor}`}>{badgeStyle.text}</Badge>
                </TableCell>
                <TableCell className="font-mono text-sm">{config.key}</TableCell>
                <TableCell className="max-w-xs truncate">{config.value}</TableCell>
                <TableCell className="max-w-md truncate text-gray-600 dark:text-gray-400">
                  {config.description || '-'}
                </TableCell>
                <TableCell className="text-sm text-gray-600 dark:text-gray-400">
                  {formatDate(config.lastUpdated)}
                </TableCell>
                <TableCell className="text-right">
                  <div className="flex justify-end gap-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => onEdit(config)}
                    >
                      <Pencil className="h-4 w-4" />
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => onDelete(config)}
                      className="text-red-600 hover:text-red-700 hover:bg-red-50 dark:hover:bg-red-950"
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </div>
                </TableCell>
              </TableRow>
            );
          })}
        </TableBody>
      </Table>
    </div>
  );
}
