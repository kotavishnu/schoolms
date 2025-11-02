import { useState, useEffect, useCallback } from 'react';

/**
 * Custom hook for fetching data with loading and error states
 * @param {Function} fetchFunction - The async function to fetch data
 * @param {Array} dependencies - Dependencies to trigger refetch
 * @param {boolean} immediate - Whether to fetch immediately (default: true)
 * @returns {Object} - { data, loading, error, refetch }
 */
export const useFetch = (fetchFunction, dependencies = [], immediate = true) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(immediate);
  const [error, setError] = useState(null);

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const result = await fetchFunction();
      setData(result.data);
      setLoading(false);
      return result.data;
    } catch (err) {
      setError(err);
      setLoading(false);
      throw err;
    }
  }, [fetchFunction]);

  useEffect(() => {
    if (immediate) {
      fetchData();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, dependencies);

  const refetch = useCallback(() => {
    return fetchData();
  }, [fetchData]);

  return { data, loading, error, refetch };
};
