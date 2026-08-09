import dayjs from 'dayjs';

const DATE_FORMAT = 'YYYY-MM-DD';
const TIME_FORMAT = 'HH:mm:ss';
const DATETIME_FORMAT = 'YYYY-MM-DD HH:mm:ss';
const DATETIME_MINUTE_FORMAT = 'YYYY-MM-DD HH:mm';
const MONTH_DAY_TIME_FORMAT = 'MM-DD HH:mm';

export function formatDate(value?: string | null) {
  return value ? dayjs(value).format(DATE_FORMAT) : '-';
}

export function formatDateTime(value?: string | null) {
  return value ? dayjs(value).format(DATETIME_FORMAT) : '-';
}

export function formatTime(value?: string | null) {
  return value ? dayjs(value).format(TIME_FORMAT) : '-';
}

export function formatDateTimeMinute(value?: string | null) {
  return value ? dayjs(value).format(DATETIME_MINUTE_FORMAT) : '-';
}

export function formatMonthDayTime(value?: string | null) {
  return value ? dayjs(value).format(MONTH_DAY_TIME_FORMAT) : '-';
}
