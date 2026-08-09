import { getAuthorization } from '@/service/request/shared';

function parseFilenameFromDisposition(disposition: string | null) {
  if (!disposition) return '';

  const utf8Match = disposition.match(/filename\*=UTF-8''([^;]+)/i);
  if (utf8Match?.[1]) {
    return decodeURIComponent(utf8Match[1]);
  }

  const plainMatch = disposition.match(/filename="?([^"]+)"?/i);
  return plainMatch?.[1] || '';
}

export async function openFileByMode(fileId: number, mode: 'preview' | 'download') {
  const response = await fetch(`/api/files/${fileId}/${mode}`, {
    headers: {
      Authorization: getAuthorization() || ''
    }
  });

  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`);
  }

  const blob = await response.blob();
  const url = URL.createObjectURL(blob);

  if (mode === 'preview') {
    window.open(url, '_blank', 'noopener,noreferrer');
    setTimeout(() => URL.revokeObjectURL(url), 5000);
    return;
  }

  const fileName = parseFilenameFromDisposition(response.headers.get('Content-Disposition'));
  const link = document.createElement('a');
  link.href = url;
  link.download = fileName || '';
  link.click();
  URL.revokeObjectURL(url);
}

export function formatFileSize(size: number | null | undefined) {
  if (!size || size < 0) return '-';

  const units = ['B', 'KB', 'MB', 'GB'];
  let next = size;
  let index = 0;

  while (next >= 1024 && index < units.length - 1) {
    next /= 1024;
    index += 1;
  }

  return `${next.toFixed(index === 0 ? 0 : 2)} ${units[index]}`;
}
