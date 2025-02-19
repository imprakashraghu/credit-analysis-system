import axios from 'axios'
import React, { useEffect, useState } from 'react'
import Loader from '../components/Loader'

function FrequencyCount() {

    const [loading, setLoading] = useState(false)
    const [data, setData] = useState(null)

    useEffect(() => {
        /**
         * Used to fetch frequencies counts from the combined csv
         */
        async function getData() {
            setLoading(true)
            axios.post('http://localhost:8000/frequencyCount', {})
            .then(response => {
                setData(response.data.frequencies)
            })
            .catch(err => console.log(err))
            .finally(_ => setLoading(false))
        }
        getData()
        return () => {}
    }, [])


    if (loading) return <Loader />

  return (
    <div className='w-full h-full bg-white flex flex-col items-center justify-start px-24 py-4 space-y-3'>
        <p className='w-full text-gray-500 text-lg'>
            Frequency Count
        </p>
        <div className='max-w-xl mx-auto py-4 grid grid-cols-5 gap-4'>
            {
                data && data?.map(item => (
                    <div key={item?.word} className='px-2 py-1 bg-green-100 text-black font-medium text-left flex flex-row items-center justify-between rounded-md space-x-2'>
                        <span>{item?.word}</span>
                        <span className='rounded-lg p-1 text-sm text-white bg-black'>{item?.freq||0}</span>
                    </div>
                ))
            }
        </div>
    </div>
  )
}

export default FrequencyCount