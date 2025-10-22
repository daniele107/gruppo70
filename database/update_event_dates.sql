-- Aggiorna le date degli eventi per metterle nel passato per il test
-- Questo permetterà di testare se la funzione di eliminazione eventi conclusi funziona

UPDATE hackathon 
SET data_fine = '2025-09-20 18:00:00'::timestamp 
WHERE nome IN ('1', 'fgsdf');

-- Verifica le modifiche
SELECT id, nome, data_inizio, data_fine, evento_concluso 
FROM hackathon 
WHERE nome IN ('1', 'fgsdf');

COMMIT;
