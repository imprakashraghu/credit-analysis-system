import React from 'react'
import { useNavigate } from 'react-router-dom'

function Card({ 
    category,
    title,
    image_url,
    annual_fee,
    purchase_fee,
    link
}) {

    const router = useNavigate()

  return (
    <div className='duration-200 flex flex-col items-center space-y-4 pb-3 justify-between border rounded-md group hover:border-green-700'>
        <div className='w-full bg-gray-100 text-black rounded-t-md font-medium flex text-sm py-1 items-center justify-center'>
            {category}
        </div>
        <img alt={title} src={image_url} className='contain w-full px-2' />
        <h2 className='w-full text-center text-md text-black px-2 font-semibold'>{title}</h2>
        <p className='w-full text-center text-sm text-gray-700 px-2'>The annual fee is just {annual_fee?.indexOf('$')!==-1?'':'$'}{annual_fee} dollars and the purchase rates are upto {purchase_fee}{purchase_fee?.indexOf('%')!==-1?'':'%'}</p>
        <button
            onClick={() => window.open(link)}
            className='w-[90%] bg-green-500 group text-white rounded-md px-3 py-1 text-sm font-medium text-center hover:bg-green-600'
        >Go to Site</button>
    </div>
  )
}

export default Card