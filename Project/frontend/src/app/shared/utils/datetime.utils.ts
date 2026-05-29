/** Parses API LocalDateTime strings as wall-clock local time (no UTC shift). */
export function parseApiDateTime(value: string): Date {
  const normalized = value.trim();
  const localMatch = /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2})(?::(\d{2}))?/.exec(normalized);

  if (localMatch && !/[zZ]|[+-]\d{2}:\d{2}$/.test(normalized)) {
    const [, year, month, day, hour, minute, second] = localMatch;
    return new Date(
      Number(year),
      Number(month) - 1,
      Number(day),
      Number(hour),
      Number(minute),
      Number(second ?? 0),
    );
  }

  return new Date(normalized);
}

/*export function formatApiDateTime(value?: string | null): string {
  if (!value) {
    return '—';
  }

  return parseApiDateTime(value).toLocaleString('bs-BA', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}*/

export function formatApiDateTime(value?: string | null): string {
  if (!value) {
    return '—';
  }

  return parseApiDateTime(value).toLocaleString('en-GB', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}

export const EUROPEAN_DATETIME_PLACEHOLDER = 'dd.MM.yyyy HH:mm';

const EUROPEAN_DATETIME_PATTERN = /^(\d{2})\.(\d{2})\.(\d{4})[ T](\d{2}):(\d{2})$/;

export function parseEuropeanDateTimeInput(value: string): string | null {
  const trimmed = value.trim();
  if (!trimmed) {
    return null;
  }

  const match = EUROPEAN_DATETIME_PATTERN.exec(trimmed);
  if (!match) {
    return null;
  }

  const [, day, month, year, hour, minute] = match;
  return `${year}-${month}-${day}T${hour}:${minute}:00`;
}

export function toEuropeanDateTimeInput(value?: string | null): string {
  if (!value) {
    return '';
  }

  const date = parseApiDateTime(value);
  const pad = (part: number) => part.toString().padStart(2, '0');

  return `${pad(date.getDate())}.${pad(date.getMonth() + 1)}.${date.getFullYear()} ${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

export function toDatetimeLocalValue(date: Date): string {
  const pad = (part: number) => part.toString().padStart(2, '0');

  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
}
