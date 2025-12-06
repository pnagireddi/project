import React from 'react';

function renderObjectTable(obj){
  const rows = Object.keys(obj||{}).map(k => ({ key: k, value: obj[k] }));
  return (
    <table className="data-table kv-table">
      <tbody>
        {rows.map(r => (
          <tr key={r.key}>
            <td className="kv-key">{r.key}</td>
            <td className="kv-value">{String(r.value === null || r.value === undefined ? '' : r.value)}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

function renderArrayTable(arr){
  const cols = Array.from(new Set(arr.flatMap(it => Object.keys(it || {}))));
  return (
    <table className="data-table">
      <thead>
        <tr>{cols.map(c => <th key={c}>{c}</th>)}</tr>
      </thead>
      <tbody>
        {arr.map((row, idx) => (
          <tr key={idx}>
            {cols.map(c => <td key={c}>{String((row && row[c]) ?? '')}</td>)}
          </tr>
        ))}
      </tbody>
    </table>
  );
}

export default function JSONTable({ data }){
  if (!data) return null;
  if (Array.isArray(data)) return renderArrayTable(data);
  if (typeof data === 'object') return renderObjectTable(data);
  return <pre className="data-raw">{String(data)}</pre>;
}
