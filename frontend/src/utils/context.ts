export function formatContextType(contextType: string | null | undefined) {
  return contextType === 'PLATFORM' ? 'Platform' : 'Tenant';
}
