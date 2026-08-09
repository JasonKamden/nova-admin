const ALLOWED_TAGS = new Set([
  'A',
  'B',
  'BLOCKQUOTE',
  'BR',
  'CODE',
  'DIV',
  'EM',
  'H1',
  'H2',
  'H3',
  'H4',
  'H5',
  'H6',
  'HR',
  'I',
  'IMG',
  'LI',
  'OL',
  'P',
  'PRE',
  'SPAN',
  'STRONG',
  'TABLE',
  'TBODY',
  'TD',
  'TH',
  'THEAD',
  'TR',
  'U',
  'UL'
]);

const URL_ATTRS = new Set(['href', 'src']);
const SAFE_ATTRS = new Set(['colspan', 'rowspan', 'target', 'rel', 'alt', 'title']);

function isSafeUrl(value: string) {
  return /^(https?:|mailto:|tel:|data:image\/)/i.test(value) || value.startsWith('/');
}

function sanitizeElement(element: Element) {
  if (!ALLOWED_TAGS.has(element.tagName)) {
    const parent = element.parentNode;
    while (element.firstChild) {
      parent?.insertBefore(element.firstChild, element);
    }
    parent?.removeChild(element);
    return;
  }

  Array.from(element.attributes).forEach(attr => {
    const name = attr.name.toLowerCase();
    const value = attr.value;

    if (name.startsWith('on') || name === 'style') {
      element.removeAttribute(attr.name);
      return;
    }

    if (URL_ATTRS.has(name)) {
      if (!isSafeUrl(value)) {
        element.removeAttribute(attr.name);
      }
      return;
    }

    if (!SAFE_ATTRS.has(name) && !name.startsWith('data-')) {
      element.removeAttribute(attr.name);
    }
  });

  if (element.tagName === 'A') {
    element.setAttribute('rel', 'noopener noreferrer nofollow');
    if (element.getAttribute('target') === '_blank') return;
    element.setAttribute('target', '_blank');
  }
}

export function sanitizeRichHtml(html: string | null | undefined) {
  if (!html) return '';

  const parser = new DOMParser();
  const document = parser.parseFromString(html, 'text/html');

  Array.from(document.body.querySelectorAll('*')).forEach(node => sanitizeElement(node));

  return document.body.innerHTML;
}
