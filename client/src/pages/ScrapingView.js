import React, { useEffect, useState } from 'react'
import Loader from '../components/Loader'
import axios from 'axios'
import useQuery from '../useQuery'
import Card from '../components/Card'

function RBC() {

    const [currentTab, setCurrentTab] = useState('Travel')
    const [loading, setLoading] = useState(false)
    const [data, setData] = useState([])

    const query = useQuery()

    useEffect(() => {
        /**
         * Used to scrap data from website based on the query string
         */
        async function getDataFromSite() {
            setLoading(true)
            axios.post('http://localhost:8000/scrap/'+query.get('site'))
            .then(response => {
                setData(response?.data?.result||[])
                setCurrentTab(response?.data?.result[0]?.category)
            })
            .catch(err => console.log(err?.message||err))
            .finally(_ => setLoading(false))
        }
        if (!loading) getDataFromSite()
        return () => {}
    }, [])


    if (loading) {
        return <Loader />
    }

  return (
    <div className='w-full h-full bg-white flex flex-col items-center justify-start px-24 py-4 space-y-3'>
        <p className='w-full text-gray-500 text-lg'>
            {query.get('title')}
        </p>
        <div className='w-full grid grid-cols-3 lg:grid-cols-5 gap-4'>
            {
                data.map(item => (
                    <Card
                        key={item.image_url}
                        title={item.title}
                        image_url={item.image_url}
                        annual_fee={item.annual_fee}
                        purchase_fee={item.purchase_fee}
                        link={item.link}
                        category={item.category}
                    />
                ))
            }
        </div>
    </div>
  )
}

export default RBC