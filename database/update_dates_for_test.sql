-- Script per aggiornare le date degli eventi al passato per testare l'eliminazione
-- Questo aggiorna tutti gli eventi con date nel futuro o oggi al 20/09/2025

UPDATE hackathon
SET data_fine = '2025-09-20 18:00:00'::timestamp
WHERE data_fine IS NOT NULL
  AND (data_fine >= CURRENT_TIMESTAMP);

-- Verifica le modifiche
SELECT id, nome, data_inizio, data_fine, evento_concluso
FROM hackathon
WHERE data_fine IS NOT NULL
ORDER BY data_fine;

COMMIT;
