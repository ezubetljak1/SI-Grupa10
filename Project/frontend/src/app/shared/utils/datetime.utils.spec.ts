import { describe, expect, it } from 'vitest';

import {
  formatApiDateTime,
  parseApiDateTime,
  parseEuropeanDateTimeInput,
  toDatetimeLocalValue,
  toEuropeanDateTimeInput,
} from './datetime.utils';

describe('datetime.utils', () => {
  it('parses API LocalDateTime values as local wall-clock time', () => {
    const parsed = parseApiDateTime('2026-06-06T14:35:20');

    expect(parsed.getFullYear()).toBe(2026);
    expect(parsed.getMonth()).toBe(5);
    expect(parsed.getDate()).toBe(6);
    expect(parsed.getHours()).toBe(14);
    expect(parsed.getMinutes()).toBe(35);
    expect(parsed.getSeconds()).toBe(20);
  });

  it('converts valid European datetime input to API format', () => {
    expect(parseEuropeanDateTimeInput('06.06.2026 14:35')).toBe(
      '2026-06-06T14:35:00'
    );
  });

  it('rejects invalid European datetime input', () => {
    expect(parseEuropeanDateTimeInput('2026-06-06 14:35')).toBeNull();
    expect(parseEuropeanDateTimeInput('06/06/2026 14:35')).toBeNull();
    expect(parseEuropeanDateTimeInput('')).toBeNull();
  });

  it('converts API datetime to European input format', () => {
    expect(toEuropeanDateTimeInput('2026-06-06T09:07:00')).toBe(
      '06.06.2026 09:07'
    );
  });

  it('converts Date to datetime-local value', () => {
    const value = new Date(2026, 5, 6, 9, 7);

    expect(toDatetimeLocalValue(value)).toBe('2026-06-06T09:07');
  });

  it('returns an em dash when formatting an empty value', () => {
    expect(formatApiDateTime(undefined)).toBe('—');
    expect(formatApiDateTime(null)).toBe('—');
  });
});