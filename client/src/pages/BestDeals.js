import axios from 'axios'
import React, { useEffect, useState } from 'react'
import Loader from '../components/Loader'
import Card from '../components/Card'

function BestDeals() {

  const [loading, setLoading] = useState(false)
  const [result, setResult] = useState([])
  const [top, setTop] = useState(5)
  const [category, setCategory] = useState(null)
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    /**
     * Used to fetch the best deals based on parameters like
     * annual fee and purchase rate fee and limit results
     */
    async function getBestDeals() {
      setLoading(true)
      axios.post('http://localhost:8000/bestDeals', { top: top||4, category: category || 'all' })
      .then(response => {
        setResult(response.data.best_deals||[])
        setCategories(response.data.categories||[])
      })
      .catch(err => console.log(err))
      .finally(_ => setLoading(false))
    }
    getBestDeals()
  }, [top, category])

  if (loading) {
        return <Loader />
    }

  return (
    <div className='w-full h-full bg-white flex flex-col items-center justify-start px-24 py-3 space-y-3'>
        <div className='w-full flex flex-row items-center space-x-4 space-y-3'>
          <p className='w-full text-gray-500 text-lg'>
              Best Deals
          </p>
          <select 
            onChange={e => setCategory(e.target.value)}
            value={category}
            className='text-md px-2 py-1 text-black'>
              <option value={'all'}>---Select Category---</option>
            {
              categories.map(item => (
                <option key={item} value={item}>{item}</option>
              ))
            }
          </select>
          <select 
            onChange={e => setTop(e.target.value)}
            value={top}
            className='text-md px-2 py-1 text-black'>
            <option key={4} value={4}>4 Results</option>
            <option key={5} value={5}>5 Results</option>
            <option key={10} value={10}>10 Results</option>
          </select>
        </div>
        <div className='w-full grid grid-cols-5 gap-4'>
          {
              result.map(item => (
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

export default BestDeals