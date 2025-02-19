import React from 'react'

function Avatar({
    fullName,
    position,
    studentId,
    imgSrc,
}) {
  return (
    <div className='flex flex-col w-full items-center space-y-1'>
        <img
            src={imgSrc}
            className='rounded-full object-cover w-32 h-32'
            alt={fullName}
        />
        <p className='w-full text-center text-black font-medium text-md leading-tight'>
            {fullName}
        </p>
        <p className='w-full text-center text-gray-500 text-sm'>
            {position}
        </p>
        <p className='w-full underline text-center text-gray-500 text-sm'>
            {studentId}
        </p>
    </div>
  )
}

export default Avatar